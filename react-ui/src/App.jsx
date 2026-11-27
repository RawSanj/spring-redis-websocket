import { useState } from "react";
import { Theme } from "@astryxdesign/core/theme";
import { themes, defaultThemeSlug } from "./astryx-themes";
import { VStack } from "@astryxdesign/core/VStack";
import { HStack } from "@astryxdesign/core/HStack";
import { Heading } from "@astryxdesign/core/Heading";
import { Text } from "@astryxdesign/core/Text";
import { Button } from "@astryxdesign/core/Button";
import { Badge } from "@astryxdesign/core/Badge";
import { Banner } from "@astryxdesign/core/Banner";
import { Card } from "@astryxdesign/core/Card";
import { Icon } from "@astryxdesign/core/Icon";
import {
  ChatComposer,
  ChatMessage,
  ChatMessageBubble,
  ChatMessageList,
  ChatMessageMetadata,
  ChatSystemMessage,
} from "@astryxdesign/core/Chat";
import {
  AtSymbolIcon,
  BoltIcon,
  CodeBracketIcon,
  GlobeAltIcon,
} from "@heroicons/react/24/outline";
import { useGroupChat } from "./chat/useGroupChat";

function MessageFeed({ entries }) {
  return (
    <ChatMessageList
      emptyState={
        <Text color="secondary">
          No messages yet. Connect and say hello.
        </Text>
      }
    >
      {entries.map((entry) =>
        entry.system ? (
          <ChatSystemMessage key={entry.key}>{entry.system}</ChatSystemMessage>
        ) : (
          <ChatMessage
            key={entry.key}
            sender={entry.own ? "user" : "assistant"}
            name={entry.hostname}
          >
            <ChatMessageBubble
              metadata={
                <ChatMessageMetadata footer={`#${entry.id}`} />
              }
            >
              {entry.message}
            </ChatMessageBubble>
          </ChatMessage>
        ),
      )}
    </ChatMessageList>
  );
}

export default function App() {
  const chat = useGroupChat();
  const [draft, setDraft] = useState("");

  const submitOverWs = (value) => {
    chat.sendWS(value);
    setDraft("");
  };

  const submitOverHttp = (value) => {
    void chat.sendHTTP(value);
    setDraft("");
  };

  const hasDraft = draft.trim().length > 0;

  return (
    <Theme theme={themes[defaultThemeSlug]}>
      <VStack hAlign="center" style={{ height: "100dvh" }}>
        <VStack
          gap={3}
          padding={10}
          width="100%"
          maxWidth={1200}
          style={{ height: "100%" }}
        >
          <Card>
            <VStack gap={2}>

				<HStack gap={2} justify="between" vAlign="center" wrap="wrap">
					<Heading level={1}>Spring Reactive Redis Chat</Heading>
					<HStack gap={2} vAlign="center">
						<Text color="secondary">Platform: {chat.platform}</Text>
						<Badge
							variant={chat.connected ? "success" : "neutral"}
							label={`${chat.usersOnline} Online`}
						/>
					</HStack>
				</HStack>
              <HStack gap={2} justify="between" vAlign="center" wrap="wrap">
                <HStack gap={2}>
                  <Button
                    label="Connect"
                    onClick={() => chat.connect()}
                    isDisabled={chat.connected}
                  />
                  <Button
                    label="Disconnect"
                    variant="destructive"
                    onClick={chat.disconnect}
                    isDisabled={!chat.connected}
                  />
                </HStack>
				  <HStack gap={2} vAlign="center">
					  <Text color="primary">Total Messages: </Text>
					  <Badge
						  variant="purple"
						  label={`${chat.totalMessages}`}
					  />
				  </HStack>
              </HStack>
            </VStack>
          </Card>

          {chat.showDisconnected && (
            <Banner
              status="warning"
              title="Websocket connection disconnected!"
              description="Please try connecting again."
              isDismissable
              onDismiss={chat.dismissNotice}
            />
          )}

          <VStack style={{ flex: 1, minHeight: 0 }} isScrollable>
            <MessageFeed entries={chat.entries} />
          </VStack>

          <ChatComposer
            value={draft}
            onChange={setDraft}
            onSubmit={submitOverWs}
            placeholder="Type a message..."
            sendActions={
              <>
                <Button
                  label="Send over websocket"
                  tooltip="Send over websocket"
                  isIconOnly
                  icon={<Icon icon={BoltIcon} size="sm" />}
                  onClick={() => submitOverWs(draft)}
                  isDisabled={!hasDraft}
                />
                <Button
                  label="Send over HTTP"
                  tooltip="Send over HTTP"
                  isIconOnly
                  icon={<Icon icon={GlobeAltIcon} size="sm" />}
                  onClick={() => submitOverHttp(draft)}
                  isDisabled={!hasDraft}
                />
              </>
            }
          />

          <HStack justify="between">
            <Button
              label="@Raw_Sanj"
              variant="secondary"
              icon={<Icon icon={AtSymbolIcon} size="md" color="blue" />}
              href="https://twitter.com/raw_sanj"
              target="_blank"
              rel="noopener noreferrer"
            />
            <Button
              label="All rights reserved by @RawSanj"
              variant="secondary"
              icon={<Icon icon={GlobeAltIcon} size="md" color="green" />}
              href="https://rawsanj.dev"
              target="_blank"
              rel="noopener noreferrer"
            />
            <Button
              label="GitHub Repo"
              variant="secondary"
              icon={<Icon icon={CodeBracketIcon} size="md" color="purple" />}
              href="https://github.com/RawSanj/spring-redis-websocket"
              target="_blank"
              rel="noopener noreferrer"
            />
          </HStack>
        </VStack>
      </VStack>
    </Theme>
  );
}
