# Actions and backend

Quill UI never mutates game state. A screen reads a state snapshot and emits actions through a
backend boundary.

## Command-first

`QuillCommandSink` sends a slash command as the local player:

```java
QuillCommandSink.get().execute("example config set page 2");
```

This is simple, auditable and works with server-side commands. Use it when the server already owns
the command surface.

## Typed backend actions

For forms and frequent toggles, implement `QuillActionSink` with a project-specific C2S payload:

```java
public final class PacketSink implements QuillActionSink {
    @Override
    public void execute(String action) {
        // parse an action id, or use a dedicated payload type
        // send it to the server; the client never writes authoritative state
    }
}
```

The server must:

1. validate permission and ranges;
2. apply the change and save;
3. return an updated state snapshot;
4. reject stale revisions or rate-limit abusive requests.

The same widgets work with either sink. This keeps gameplay state server-authoritative while
allowing a UI without chat prompts.
