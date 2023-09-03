const sqlite = require('better-sqlite3');
const path = require('path');
const db = new sqlite(path.resolve('api-data.db'), {fileMustExist: true});

function get(sql, param) {
  return db.prepare(sql).get(param);
}

function query(sql, params) {
  return db.prepare(sql).all(params);
}

function run(sql, params) {
  return db.prepare(sql).run(params);
}

module.exports = {
  query,
  run,
  get
}