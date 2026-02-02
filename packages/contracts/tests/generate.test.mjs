import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import test from 'node:test';
import { createGeneratedSource } from '../scripts/generate.mjs';

const rootDir = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const openApiPath = path.join(rootDir, 'openapi.json');
const generatedPath = path.join(rootDir, 'src', 'generated.ts');

test('generated types stay in sync with OpenAPI spec', () => {
  const spec = JSON.parse(readFileSync(openApiPath, 'utf8'));
  const expected = createGeneratedSource(spec);
  const actual = readFileSync(generatedPath, 'utf8');
  assert.equal(actual, expected);
});
