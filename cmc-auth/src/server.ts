import { createServer } from "node:http";

const cmcDbUrl = process.env.CMC_DB_URL;

if (!cmcDbUrl) {
  console.error("CMC_DB_URL is required to start cmc-auth");
  process.exit(1);
}

const port = Number(process.env.PORT ?? 4000);
const server = createServer((_req, res) => {
  res.writeHead(200, { "content-type": "application/json" });
  res.end(JSON.stringify({ status: "ok" }));
});

server.listen(port, () => {
  console.log(`cmc-auth listening on port ${port}`);
});
