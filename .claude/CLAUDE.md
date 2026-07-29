# CLAUDE.md

## Processes

Never kill processes by name. Stop only the exact PID you started this session (`Start-Process -PassThru`), or let it exit itself (`timeout`/foreground).
