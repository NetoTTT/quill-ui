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

For forms and frequent toggles, implement `QuillActionSink<A>` with a project-specific payload type:

```java
public enum ConfigAction {
    NEXT_PAGE,
    PREVIOUS_PAGE
}

public final class PacketSink implements QuillActionSink<ConfigAction> {
    @Override
    public void execute(ConfigAction action) {
        // encode the typed action and send it to the server;
        // the client never writes authoritative state
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
