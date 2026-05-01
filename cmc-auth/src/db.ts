import crypto from "node:crypto";
import { Pool, type QueryResult, type QueryResultRow } from "pg";

const isPgMemMode = process.env.CMC_AUTH_USE_PGMEM === "1";

export const db = isPgMemMode ? createPgMemPool() : createPostgresPool();

export function query<T extends QueryResultRow>(
  text: string,
  params: unknown[] = [],
): Promise<QueryResult<T>> {
  return db.query<T>(text, params);
}

function createPostgresPool(): Pool {
  if (!process.env.CMC_DB_URL) {
    throw new Error("CMC_DB_URL is required to initialize database pool");
  }

  return new Pool({
    connectionString: process.env.CMC_DB_URL,
  });
}

function createPgMemPool(): Pool {
  // Lazy require keeps pg-mem isolated to explicit test mode.
  // eslint-disable-next-line @typescript-eslint/no-require-imports
  const { newDb, DataType } = require("pg-mem") as any;
  const memoryDb = newDb();

  memoryDb.public.registerFunction({
    name: "gen_random_uuid",
    returns: DataType.uuid,
    implementation: () => crypto.randomUUID(),
    impure: true,
  });

  const pgMem = memoryDb.adapters.createPg();
  return new pgMem.Pool();
}
