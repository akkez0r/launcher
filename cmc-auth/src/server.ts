import cors from "cors";
import dotenv from "dotenv";
import express from "express";

dotenv.config();

if (!process.env.CMC_DB_URL) {
  throw new Error("CMC_DB_URL is required to start cmc-auth");
}

const app = express();
const port = Number(process.env.PORT ?? 8787);
const allowedOrigins = new Set(
  (process.env.CORS_ALLOWED_ORIGINS ?? "")
    .split(",")
    .map((origin) => origin.trim())
    .filter(Boolean),
);

app.use(
  cors({
    origin(origin, callback) {
      if (!origin || allowedOrigins.has(origin)) {
        callback(null, true);
        return;
      }

      callback(new Error("Origin not allowed by CORS"));
    },
  }),
);
app.use(express.json());

app.get("/health", (_req, res) => {
  res.json({ ok: true });
});

app.listen(port, () => {
  console.log(`cmc-auth listening on ${port}`);
});
