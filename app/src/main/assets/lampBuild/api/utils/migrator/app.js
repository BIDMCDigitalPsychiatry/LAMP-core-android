"use strict";
var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
var nano_1 = __importDefault(require("nano"));
var migrators_1 = require("./migrators");
var nano = nano_1.default(process.env.CDB_PARAM || "");
var sleep = function (time) { return new Promise(function (resolve) { return setTimeout(resolve, time); }); };
function migrator() {
    return __awaiter(this, void 0, void 0, function () {
        function existsDB(name) {
            return availableDBs.indexOf(name) >= 0;
        }
        function updateToken(token) {
            return __awaiter(this, void 0, void 0, function () {
                var renewedToken;
                return __generator(this, function (_a) {
                    switch (_a.label) {
                        case 0:
                            token = token || {}; // TODO: set to await Changes()
                            return [4 /*yield*/, nano.use("root").insert(__assign(__assign({}, token), { _id: "sync_token" }))];
                        case 1:
                            renewedToken = _a.sent();
                            console.dir(renewedToken);
                            return [2 /*return*/, renewedToken];
                    }
                });
            });
        }
        function createDB(name, documents) {
            return __awaiter(this, void 0, void 0, function () {
                var _a, _b, _c, _d, _e, _f, _g, token, _h, _j, _k, _l, _m, _o;
                var _p, _q;
                return __generator(this, function (_r) {
                    switch (_r.label) {
                        case 0:
                            console.group();
                            console.time();
                            console.log("-- STARTING [" + name + "] --");
                            _b = (_a = console).dir;
                            return [4 /*yield*/, nano.db.create(name)];
                        case 1:
                            _b.apply(_a, [_r.sent()]);
                            if (!!!documents) return [3 /*break*/, 4];
                            _d = (_c = console).dir;
                            _f = (_e = nano.use(name)).bulk;
                            _g = {};
                            return [4 /*yield*/, documents()];
                        case 2: return [4 /*yield*/, _f.apply(_e, [(_g.docs = _r.sent(),
                                    _g)])];
                        case 3:
                            _d.apply(_c, [_r.sent()]);
                            return [3 /*break*/, 5];
                        case 4:
                            console.dir({ ok: true, message: "no documents to create" });
                            _r.label = 5;
                        case 5:
                            console.log("-- FINISHED [" + name + "] --");
                            if (!(name !== "root")) return [3 /*break*/, 10];
                            console.log("-- CREATING BIDI SYNC TOKEN [" + name + "] --");
                            return [4 /*yield*/, nano.use("root").get("sync_token")];
                        case 6:
                            token = (_r.sent());
                            _h = token;
                            _j = [__assign({}, (token.cdb || {}))];
                            _p = {};
                            _k = name;
                            return [4 /*yield*/, nano.db.changes(name, { since: "now" })];
                        case 7:
                            _h.cdb = __assign.apply(void 0, _j.concat([(_p[_k] = (_r.sent()).last_seq, _p)]));
                            _l = token;
                            _m = [__assign({}, (token.sql || {}))];
                            _q = {};
                            _o = name;
                            return [4 /*yield*/, migrators_1.Changes()];
                        case 8:
                            _l.sql = __assign.apply(void 0, _m.concat([(_q[_o] = _r.sent(), _q)]));
                            console.dir(token);
                            return [4 /*yield*/, updateToken(token)];
                        case 9:
                            _r.sent();
                            console.log("-- CREATED LATEST SYNC TOKEN --");
                            _r.label = 10;
                        case 10:
                            console.timeEnd();
                            console.groupEnd();
                            return [4 /*yield*/, nano.db.list()];
                        case 11:
                            availableDBs = _r.sent();
                            return [2 /*return*/];
                    }
                });
            });
        }
        function biDiSyncUnit(name, backward, forward) {
            return __awaiter(this, void 0, void 0, function () {
                var token, token_bak, _a, _b, _c, _d;
                return __generator(this, function (_e) {
                    switch (_e.label) {
                        case 0:
                            console.group();
                            console.time();
                            return [4 /*yield*/, nano.use("root").get("sync_token")];
                        case 1:
                            token = (_e.sent());
                            token_bak = JSON.parse(JSON.stringify(token)) /* deep clone */;
                            console.log("-- STARTING BACKWARD FLUSH [" + name + "] --");
                            console.group();
                            _a = token.cdb;
                            _b = name;
                            return [4 /*yield*/, backward(token.cdb[name] || "now")];
                        case 2:
                            _a[_b] = _e.sent();
                            console.groupEnd();
                            console.log("-- FINISHED BACKWARD FLUSH [" + name + "] --");
                            console.log("-- STARTING FORWARD SYNC [" + name + "] --");
                            console.group();
                            _c = token.sql;
                            _d = name;
                            return [4 /*yield*/, forward(token.sql[name] || "0")];
                        case 3:
                            _c[_d] = _e.sent();
                            console.groupEnd();
                            console.log("-- FINISHED FORWARD SYNC [" + name + "] --");
                            if (!(token.cdb[name] !== token_bak.cdb[name] || token.sql[name] !== token_bak.sql[name])) return [3 /*break*/, 5];
                            return [4 /*yield*/, updateToken(token)];
                        case 4:
                            _e.sent();
                            return [3 /*break*/, 6];
                        case 5:
                            console.log("-- NO SYNC TOKEN CHANGE DETECTED --");
                            _e.label = 6;
                        case 6:
                            console.timeEnd();
                            console.groupEnd();
                            return [2 /*return*/];
                    }
                });
            });
        }
        function oneWaySync() {
            return __awaiter(this, void 0, void 0, function () {
                var _this = this;
                return __generator(this, function (_a) {
                    switch (_a.label) {
                        case 0:
                            console.log("-- STARTING ONEWAY SYNC --");
                            if (!!existsDB("root")) return [3 /*break*/, 3];
                            return [4 /*yield*/, createDB("root")];
                        case 1:
                            _a.sent();
                            return [4 /*yield*/, updateToken()];
                        case 2:
                            _a.sent();
                            _a.label = 3;
                        case 3:
                            if (!!existsDB("researcher")) return [3 /*break*/, 5];
                            return [4 /*yield*/, createDB("researcher", function () { return __awaiter(_this, void 0, void 0, function () { return __generator(this, function (_a) {
                                    switch (_a.label) {
                                        case 0: return [4 /*yield*/, migrators_1.Researcher._select()];
                                        case 1: return [2 /*return*/, _a.sent()];
                                    }
                                }); }); })];
                        case 4:
                            _a.sent();
                            _a.label = 5;
                        case 5:
                            if (!!existsDB("participant")) return [3 /*break*/, 7];
                            return [4 /*yield*/, createDB("participant", function () { return __awaiter(_this, void 0, void 0, function () { return __generator(this, function (_a) {
                                    switch (_a.label) {
                                        case 0: return [4 /*yield*/, migrators_1.Participant._select()];
                                        case 1: return [2 /*return*/, _a.sent()];
                                    }
                                }); }); })];
                        case 6:
                            _a.sent();
                            _a.label = 7;
                        case 7:
                            if (!!existsDB("activity")) return [3 /*break*/, 9];
                            return [4 /*yield*/, createDB("activity", function () { return __awaiter(_this, void 0, void 0, function () { return __generator(this, function (_a) {
                                    switch (_a.label) {
                                        case 0: return [4 /*yield*/, migrators_1.Activity._select()];
                                        case 1: return [2 /*return*/, _a.sent()];
                                    }
                                }); }); })];
                        case 8:
                            _a.sent();
                            _a.label = 9;
                        case 9:
                            if (!!existsDB("activity_event")) return [3 /*break*/, 11];
                            return [4 /*yield*/, createDB("activity_event", function () { return __awaiter(_this, void 0, void 0, function () { return __generator(this, function (_a) {
                                    switch (_a.label) {
                                        case 0: return [4 /*yield*/, migrators_1.ActivityEvent._select()];
                                        case 1: return [2 /*return*/, _a.sent()];
                                    }
                                }); }); })];
                        case 10:
                            _a.sent();
                            _a.label = 11;
                        case 11:
                            if (!!existsDB("sensor_event")) return [3 /*break*/, 13];
                            return [4 /*yield*/, createDB("sensor_event", function () { return __awaiter(_this, void 0, void 0, function () { return __generator(this, function (_a) {
                                    switch (_a.label) {
                                        case 0: return [4 /*yield*/, migrators_1.SensorEvent._select()];
                                        case 1: return [2 /*return*/, _a.sent()];
                                    }
                                }); }); })];
                        case 12:
                            _a.sent();
                            _a.label = 13;
                        case 13:
                            console.log("-- FINISHED ONEWAY SYNC --");
                            return [2 /*return*/];
                    }
                });
            });
        }
        function biDiSync() {
            return __awaiter(this, void 0, void 0, function () {
                var _this = this;
                return __generator(this, function (_a) {
                    switch (_a.label) {
                        case 0:
                            console.log("-- STARTING BIDI SYNC --");
                            if (!existsDB("researcher")) return [3 /*break*/, 2];
                            return [4 /*yield*/, biDiSyncUnit("researcher", function (change_version) { return __awaiter(_this, void 0, void 0, function () {
                                    var changes, results, reconcilations, _a, _b;
                                    return __generator(this, function (_c) {
                                        switch (_c.label) {
                                            case 0: return [4 /*yield*/, nano.db.changes("researcher", { since: change_version, include_docs: true })];
                                            case 1:
                                                changes = _c.sent();
                                                console.dir({ changes: changes.results.map(function (x) { return ({ id: x.id, seq: x.seq }); }) });
                                                if (changes.results.length === 0)
                                                    return [2 /*return*/, changes.last_seq];
                                                results = changes.results.map(function (x) { return x.doc; });
                                                return [4 /*yield*/, migrators_1.Researcher._upsert(results)];
                                            case 2:
                                                reconcilations = _c.sent();
                                                console.dir(reconcilations);
                                                _b = (_a = console).dir;
                                                return [4 /*yield*/, nano.use("researcher").bulk({
                                                        docs: results
                                                            .map(function (x) { return (__assign(__assign({}, x), { $_rec: reconcilations.find(function (y) { return y.SourceID === x._id && y.Action === "INSERT"; }) })); })
                                                            .filter(function (x) { return !!x.$_rec; })
                                                            .map(function (x) { return (__assign(__assign({}, x), { $_rec: undefined, $_sync_id: x.$_rec.TargetID })); })
                                                    })];
                                            case 3:
                                                _b.apply(_a, [_c.sent()]);
                                                return [4 /*yield*/, nano.db.changes("researcher", { since: "now" })];
                                            case 4: return [2 /*return*/, (_c.sent()).last_seq];
                                        }
                                    });
                                }); }, function (change_version) { return __awaiter(_this, void 0, void 0, function () {
                                    var _a, _b, _c, _d, _e;
                                    return __generator(this, function (_f) {
                                        switch (_f.label) {
                                            case 0:
                                                _b = (_a = console).dir;
                                                _d = (_c = nano.use("researcher")).bulk;
                                                _e = {};
                                                return [4 /*yield*/, migrators_1.Researcher._select(change_version)];
                                            case 1: return [4 /*yield*/, _d.apply(_c, [(_e.docs = _f.sent(),
                                                        _e)])];
                                            case 2:
                                                _b.apply(_a, [_f.sent()]);
                                                return [4 /*yield*/, migrators_1.Changes()];
                                            case 3: return [2 /*return*/, _f.sent()];
                                        }
                                    });
                                }); })];
                        case 1:
                            _a.sent();
                            _a.label = 2;
                        case 2:
                            if (!existsDB("participant")) return [3 /*break*/, 4];
                            return [4 /*yield*/, biDiSyncUnit("participant", function (change_version) { return __awaiter(_this, void 0, void 0, function () {
                                    var changes;
                                    return __generator(this, function (_a) {
                                        switch (_a.label) {
                                            case 0: return [4 /*yield*/, nano.db.changes("participant", { since: change_version })
                                                /* insert */
                                                /* update */
                                                /* delete */
                                            ];
                                            case 1:
                                                changes = _a.sent();
                                                /* insert */
                                                /* update */
                                                /* delete */
                                                return [2 /*return*/, changes.last_seq];
                                        }
                                    });
                                }); }, function (change_version) { return __awaiter(_this, void 0, void 0, function () {
                                    var _a, _b, _c, _d, _e;
                                    return __generator(this, function (_f) {
                                        switch (_f.label) {
                                            case 0:
                                                _b = (_a = console).dir;
                                                _d = (_c = nano.use("participant")).bulk;
                                                _e = {};
                                                return [4 /*yield*/, migrators_1.Participant._select(change_version)];
                                            case 1: return [4 /*yield*/, _d.apply(_c, [(_e.docs = _f.sent(),
                                                        _e)])];
                                            case 2:
                                                _b.apply(_a, [_f.sent()]);
                                                return [4 /*yield*/, migrators_1.Changes()];
                                            case 3: return [2 /*return*/, _f.sent()];
                                        }
                                    });
                                }); })];
                        case 3:
                            _a.sent();
                            _a.label = 4;
                        case 4:
                            if (!existsDB("activity")) return [3 /*break*/, 6];
                            return [4 /*yield*/, biDiSyncUnit("activity", function (change_version) { return __awaiter(_this, void 0, void 0, function () {
                                    var changes;
                                    return __generator(this, function (_a) {
                                        switch (_a.label) {
                                            case 0: return [4 /*yield*/, nano.db.changes("activity", { since: change_version })
                                                /* insert */
                                                /* update */
                                                /* delete */
                                            ];
                                            case 1:
                                                changes = _a.sent();
                                                /* insert */
                                                /* update */
                                                /* delete */
                                                return [2 /*return*/, changes.last_seq];
                                        }
                                    });
                                }); }, function (change_version) { return __awaiter(_this, void 0, void 0, function () {
                                    var _a, _b, _c, _d, _e;
                                    return __generator(this, function (_f) {
                                        switch (_f.label) {
                                            case 0:
                                                _b = (_a = console).dir;
                                                _d = (_c = nano.use("activity")).bulk;
                                                _e = {};
                                                return [4 /*yield*/, migrators_1.Activity._select(change_version)];
                                            case 1: return [4 /*yield*/, _d.apply(_c, [(_e.docs = _f.sent(),
                                                        _e)])];
                                            case 2:
                                                _b.apply(_a, [_f.sent()]);
                                                return [4 /*yield*/, migrators_1.Changes()];
                                            case 3: return [2 /*return*/, _f.sent()];
                                        }
                                    });
                                }); })];
                        case 5:
                            _a.sent();
                            _a.label = 6;
                        case 6:
                            if (!existsDB("activity_event")) return [3 /*break*/, 8];
                            return [4 /*yield*/, biDiSyncUnit("activity_event", function (change_version) { return __awaiter(_this, void 0, void 0, function () {
                                    return __generator(this, function (_a) {
                                        // NOTE: Does not handle INSERT, UPDATE, or DELETE operations.
                                        return [2 /*return*/, change_version];
                                    });
                                }); }, function (change_version) { return __awaiter(_this, void 0, void 0, function () {
                                    var _a, _b, _c, _d, _e;
                                    return __generator(this, function (_f) {
                                        switch (_f.label) {
                                            case 0:
                                                // NOTE: Does not handle UPDATE or DELETE operations.
                                                _b = (_a = console).dir;
                                                _d = (_c = nano.use("activity_event")).bulk;
                                                _e = {};
                                                return [4 /*yield*/, migrators_1.ActivityEvent._select(change_version)];
                                            case 1: return [4 /*yield*/, _d.apply(_c, [(_e.docs = _f.sent(),
                                                        _e)])];
                                            case 2:
                                                // NOTE: Does not handle UPDATE or DELETE operations.
                                                _b.apply(_a, [_f.sent()]);
                                                return [4 /*yield*/, migrators_1.Changes()];
                                            case 3: return [2 /*return*/, _f.sent()];
                                        }
                                    });
                                }); })];
                        case 7:
                            _a.sent();
                            _a.label = 8;
                        case 8:
                            if (!existsDB("sensor_event")) return [3 /*break*/, 10];
                            return [4 /*yield*/, biDiSyncUnit("sensor_event", function (change_version) { return __awaiter(_this, void 0, void 0, function () {
                                    return __generator(this, function (_a) {
                                        // NOTE: Does not handle INSERT, UPDATE or DELETE operations.
                                        return [2 /*return*/, change_version];
                                    });
                                }); }, function (change_version) { return __awaiter(_this, void 0, void 0, function () {
                                    var _a, _b, _c, _d, _e;
                                    return __generator(this, function (_f) {
                                        switch (_f.label) {
                                            case 0:
                                                // NOTE: Does not handle UPDATE or DELETE operations.
                                                _b = (_a = console).dir;
                                                _d = (_c = nano.use("sensor_event")).bulk;
                                                _e = {};
                                                return [4 /*yield*/, migrators_1.SensorEvent._select(change_version)];
                                            case 1: return [4 /*yield*/, _d.apply(_c, [(_e.docs = _f.sent(),
                                                        _e)])];
                                            case 2:
                                                // NOTE: Does not handle UPDATE or DELETE operations.
                                                _b.apply(_a, [_f.sent()]);
                                                return [4 /*yield*/, migrators_1.Changes()];
                                            case 3: return [2 /*return*/, _f.sent()];
                                        }
                                    });
                                }); })];
                        case 9:
                            _a.sent();
                            _a.label = 10;
                        case 10:
                            console.log("-- FINISHED BIDI SYNC --");
                            return [2 /*return*/];
                    }
                });
            });
        }
        var availableDBs, error_1;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, nano.db.list()];
                case 1:
                    availableDBs = _a.sent();
                    console.log("-- INITIALIZING --");
                    return [4 /*yield*/, oneWaySync()];
                case 2:
                    _a.sent();
                    _a.label = 3;
                case 3:
                    if (!true) return [3 /*break*/, 9];
                    _a.label = 4;
                case 4:
                    _a.trys.push([4, 6, , 7]);
                    return [4 /*yield*/, biDiSync()];
                case 5:
                    _a.sent();
                    return [3 /*break*/, 7];
                case 6:
                    error_1 = _a.sent();
                    console.dir({ error: error_1 });
                    return [3 /*break*/, 7];
                case 7:
                    console.log("-- COOLDOWN PAUSE --");
                    return [4 /*yield*/, sleep(10 * 1000)];
                case 8:
                    _a.sent();
                    return [3 /*break*/, 3];
                case 9: return [2 /*return*/];
            }
        });
    });
}
exports.default = migrator;
// ActivityEvent + SensorEvent:
// 	 - Does not replicate UPDATE/DELETE operations from SQL.
//   - SensorEvent -> watch UserDevices.*/AppVersion/LastLogin here
//	 - attachments for UserSettings.* -> ???
// activity:
// schedule, settings, deletion, parent
// Admin_Settings.ReminderClearInterval
// if settings value contains a known key, update it, otherwise bail
// if spec is a known value, create new activity, otherwise bail
// TODO: Credential tracking
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiYXBwLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vc3JjL3V0aWxzL21pZ3JhdG9yL2FwcC50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7O0FBQUEsOENBQXdDO0FBQ3hDLHlDQVVvQjtBQUVwQixJQUFNLElBQUksR0FBRyxjQUFJLENBQUMsT0FBTyxDQUFDLEdBQUcsQ0FBQyxTQUFTLElBQUksRUFBRSxDQUFnQixDQUFBO0FBQzdELElBQU0sS0FBSyxHQUFHLFVBQUMsSUFBWSxJQUFLLE9BQUEsSUFBSSxPQUFPLENBQUMsVUFBQSxPQUFPLElBQUksT0FBQSxVQUFVLENBQUMsT0FBTyxFQUFFLElBQUksQ0FBQyxFQUF6QixDQUF5QixDQUFDLEVBQWpELENBQWlELENBQUE7QUFFakYsU0FBOEIsUUFBUTs7UUFHcEMsU0FBUyxRQUFRLENBQUMsSUFBWTtZQUM1QixPQUFPLFlBQVksQ0FBQyxPQUFPLENBQUMsSUFBSSxDQUFDLElBQUksQ0FBQyxDQUFBO1FBQ3hDLENBQUM7UUFDRCxTQUFlLFdBQVcsQ0FBQyxLQUFXOzs7Ozs7NEJBQ3BDLEtBQUssR0FBRyxLQUFLLElBQUksRUFBRSxDQUFBLENBQUMsK0JBQStCOzRCQUNoQyxxQkFBTSxJQUFJLENBQUMsR0FBRyxDQUFDLE1BQU0sQ0FBQyxDQUFDLE1BQU0sQ0FBQyxzQkFBSyxLQUFLLEtBQUUsR0FBRyxFQUFFLFlBQVksR0FBUyxDQUFDLEVBQUE7OzRCQUFwRixZQUFZLEdBQUcsU0FBcUU7NEJBQ3hGLE9BQU8sQ0FBQyxHQUFHLENBQUMsWUFBWSxDQUFDLENBQUE7NEJBQ3pCLHNCQUFPLFlBQVksRUFBQTs7OztTQUNwQjtRQUNELFNBQWUsUUFBUSxDQUFDLElBQVksRUFBRSxTQUFnQzs7Ozs7Ozs0QkFDcEUsT0FBTyxDQUFDLEtBQUssRUFBRSxDQUFBOzRCQUNmLE9BQU8sQ0FBQyxJQUFJLEVB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