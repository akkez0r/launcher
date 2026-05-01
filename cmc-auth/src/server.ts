import cors from "cors";
import dotenv from "dotenv";
import express from "express";

dotenv.config();

if (!process.env.CMC_DB_URL) {
  throw new Error("CMC_DB_URL is required to start cmc-auth");
}

const app = express();
const port = Number(process.env.PORT ?? 8787);

app.use(cors());
app.use(express.json());

app.get("/health", (_req, res) => {
  res.json({ ok: true });
});

app.listen(port, () => {
  console.log(`cmc-auth listening on port ${port}`);
});
