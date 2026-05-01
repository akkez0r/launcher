import { Pool, type QueryResult, type QueryResultRow } from "pg";

if (!process.env.CMC_DB_URL) {
  throw new Error("CMC_DB_URL is required to initialize database pool");
}

export const db = new Pool({
  connectionString: process.env.CMC_DB_URL,
});

export function query<T extends QueryResultRow>(
  text: string,
  params: unknown[] = [],
): Promise<QueryResult<T>> {
  return db.query<T>(text, params);
}
