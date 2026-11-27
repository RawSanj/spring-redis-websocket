import { useCallback, useEffect, useRef, useState } from "react";

function socketUrl() {
  const protocol = window.location.protocol === "https:" ? "wss://" : "ws://";
  return `${protocol}${window.location.host}/redis-chat`;
}

/**
 * Group-chat state for the Redis pub/sub chat. Mirrors the old jQuery UI:
 * connect/disconnect, online-users count, total message count, the message
 * feed (id, text, sending host), both send paths (websocket + HTTP POST),
 * and join/leave presence events.
 */
export function useGroupChat() {
  const [connected, setConnected] = useState(false);
  const [showDisconnected, setShowDisconnected] = useState(false);
  const [entries, setEntries] = useState([]);
  const [usersOnline, setUsersOnline] = useState(0);
  const [totalMessages, setTotalMessages] = useState(0);
  const [platform, setPlatform] = useState("JVM");
  const socketRef = useRef(null);
  const pendingRef = useRef([]);
  const keyRef = useRef(0);

  const pushSystem = useCallback((text) => {
    keyRef.current += 1;
    const key = `sys-${keyRef.current}`;
    setEntries((prev) => [...prev, { key, system: text }]);
  }, []);

  const connect = useCallback(
    (afterOpen) => {
      const current = socketRef.current;
      if (
        current &&
        (current.readyState === WebSocket.OPEN ||
          current.readyState === WebSocket.CONNECTING)
      ) {
        if (current.readyState === WebSocket.OPEN && afterOpen) afterOpen();
        return;
      }
      setShowDisconnected(false);
      const next = new WebSocket(socketUrl());
      socketRef.current = next;

      next.onopen = () => {
        setConnected(true);
        setShowDisconnected(false);
        setEntries([]);
        if (afterOpen) afterOpen();
      };

      next.onmessage = (event) => {
        let chat;
        try {
          chat = JSON.parse(event.data);
        } catch {
          return;
        }
        if (chat.id !== 0) {
          setTotalMessages(chat.id);
          setUsersOnline(chat.usersOnline ?? 0);
          const pending = pendingRef.current;
          const at = pending.indexOf(chat.message);
          let own = false;
          if (at !== -1) {
            pending.splice(at, 1);
            own = true;
          }
          setEntries((prev) => [
            ...prev,
            {
              key: `msg-${chat.id}`,
              id: chat.id,
              message: chat.message,
              hostname: chat.hostname,
              own,
            },
          ]);
        } else {
          setUsersOnline(chat.usersOnline ?? 0);
          pushSystem(
            chat.message === "CONNECTED"
              ? "A user connected"
              : "A user disconnected",
          );
        }
      };

      const handleClose = () => {
        if (socketRef.current === next) socketRef.current = null;
        setConnected(false);
        setTotalMessages(0);
        setEntries([]);
        setShowDisconnected(true);
      };
      next.onerror = handleClose;
      next.onclose = handleClose;
    },
    [pushSystem],
  );

  const disconnect = useCallback(() => {
    const socket = socketRef.current;
    socketRef.current = null;
    if (socket && socket.readyState === WebSocket.OPEN) socket.close();
    setConnected(false);
    setTotalMessages(0);
    setEntries([]);
    setShowDisconnected(true);
  }, []);

  const sendWS = useCallback(
    (text) => {
      const message = text.trim();
      if (!message) return;
      const socket = socketRef.current;
      if (socket && socket.readyState === WebSocket.OPEN) {
        pendingRef.current.push(message);
        socket.send(message);
      } else {
        connect(() => {
          const open = socketRef.current;
          if (open && open.readyState === WebSocket.OPEN) {
            pendingRef.current.push(message);
            open.send(message);
          }
        });
      }
    },
    [connect],
  );

  const sendHTTP = useCallback(async (text) => {
    const message = text.trim();
    if (!message) return;
    pendingRef.current.push(message);
    try {
      await fetch("/message", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ message }),
      });
    } catch {
      const at = pendingRef.current.indexOf(message);
      if (at !== -1) pendingRef.current.splice(at, 1);
    }
  }, []);

  const dismissNotice = useCallback(() => setShowDisconnected(false), []);

  useEffect(() => {
    fetch("/management/info")
      .then((response) => response.json())
      .then((data) => {
        if (data && data.platform) setPlatform(data.platform);
      })
      .catch(() => {});
    return () => {
      const socket = socketRef.current;
      socketRef.current = null;
      if (socket) socket.close();
    };
  }, []);

  return {
    connected,
    showDisconnected,
    dismissNotice,
    entries,
    usersOnline,
    totalMessages,
    platform,
    connect,
    disconnect,
    sendWS,
    sendHTTP,
  };
}
