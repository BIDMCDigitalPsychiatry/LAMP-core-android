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
var __values = (this && this.__values) || function(o) {
    var s = typeof Symbol === "function" && Symbol.iterator, m = s && o[s], i = 0;
    if (m) return m.call(o);
    if (o && typeof o.length === "number") return {
        next: function () {
            if (o && i >= o.length) o = void 0;
            return { value: o && o[i++], done: !o };
        }
    };
    throw new TypeError(s ? "Object is not iterable." : "Symbol.iterator is not defined.");
};
var __read = (this && this.__read) || function (o, n) {
    var m = typeof Symbol === "function" && o[Symbol.iterator];
    if (!m) return o;
    var i = m.call(o), r, ar = [], e;
    try {
        while ((n === void 0 || n-- > 0) && !(r = i.next()).done) ar.push(r.value);
    }
    catch (error) { e = { error: error }; }
    finally {
        try {
            if (r && !r.done && (m = i["return"])) m.call(i);
        }
        finally { if (e) throw e.error; }
    }
    return ar;
};
var __spread = (this && this.__spread) || function () {
    for (var ar = [], i = 0; i < arguments.length; i++) ar = ar.concat(__read(arguments[i]));
    return ar;
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
var app_1 = require("../app");
var ScriptRunner_1 = __importDefault(require("../utils/ScriptRunner"));
var mssql_1 = __importDefault(require("mssql"));
var Participant_1 = require("../model/Participant");
var Type_1 = require("../model/Type");
var ResearcherRepository_1 = require("../repository/ResearcherRepository");
var ParticipantRepository_1 = require("../repository/ParticipantRepository");
var StudyRepository_1 = require("../repository/StudyRepository");
var ActivityRepository_1 = require("../repository/ActivityRepository");
function Identifier_pack(components) {
    if (components.length === 0)
        return "";
    return Buffer.from(components.join(":")).toString("base64").replace(/=/g, "~");
}
exports.Identifier_pack = Identifier_pack;
function Identifier_unpack(components) {
    if (components.match(/^G?U\d+$/))
        return [];
    return Buffer.from(components.replace(/~/g, "="), "base64")
        .toString("utf8")
        .split(":");
}
exports.Identifier_unpack = Identifier_unpack;
var TypeRepository = /** @class */ (function () {
    function TypeRepository() {
    }
    TypeRepository._parent = function (type_id) {
        return __awaiter(this, void 0, void 0, function () {
            var result, _a, _b, parent_type, _c, _d, e_1_1;
            var e_1, _e;
            return __generator(this, function (_f) {
                switch (_f.label) {
                    case 0:
                        result = {} // obj['#parent'] === [null, undefined] -> top-level object
                        ;
                        _f.label = 1;
                    case 1:
                        _f.trys.push([1, 6, 7, 8]);
                        _a = __values(TypeRepository._parent_type(type_id)), _b = _a.next();
                        _f.label = 2;
                    case 2:
                        if (!!_b.done) return [3 /*break*/, 5];
                        parent_type = _b.value;
                        _c = result;
                        _d = parent_type;
                        return [4 /*yield*/, TypeRepository._parent_id(type_id, parent_type)];
                    case 3:
                        _c[_d] = _f.sent();
                        _f.label = 4;
                    case 4:
                        _b = _a.next();
                        return [3 /*break*/, 2];
                    case 5: return [3 /*break*/, 8];
                    case 6:
                        e_1_1 = _f.sent();
                        e_1 = { error: e_1_1 };
                        return [3 /*break*/, 8];
                    case 7:
                        try {
                            if (_b && !_b.done && (_e = _a.return)) _e.call(_a);
                        }
                        finally { if (e_1) throw e_1.error; }
                        return [7 /*endfinally*/];
                    case 8: return [2 /*return*/, result];
                }
            });
        });
    };
    /**
     * Get the self type of a given ID.
     */
    TypeRepository._self_type = function (type_id) {
        var components = Identifier_unpack(type_id);
        var from_type = components.length === 0 ? Participant_1.Participant.name : components[0];
        return from_type;
    };
    /**
     * Get all parent types of a given ID.
     */
    TypeRepository._parent_type = function (type_id) {
        var parent_types = {
            Researcher: [],
            Study: ["Researcher"],
            Participant: ["Study", "Researcher"],
            Activity: ["Study", "Researcher"],
        };
        /*
            // TODO:
            const shortcode_map = {
                'Researcher': 'R',
                'R': 'Researcher',
                'Study': 'S',
                'S': 'Study',
                'Participant': 'P',
                'P': 'Participant',
                'Activity': 'A',
                'A': 'Activity',
            }
            */
        return parent_types[TypeRepository._self_type(type_id)];
    };
    /**
     * Get a single parent object ID for a given ID.
     */
    TypeRepository._parent_id = function (type_id, type) {
        return __awaiter(this, void 0, void 0, function () {
            var self_type;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        self_type = {
                            Researcher: ResearcherRepository_1.ResearcherRepository,
                            Study: StudyRepository_1.StudyRepository,
                            Participant: ParticipantRepository_1.ParticipantRepository,
                            Activity: ActivityRepository_1.ActivityRepository,
                        };
                        return [4 /*yield*/, self_type[TypeRepository._self_type(type_id)]._parent_id(type_id, self_type[type])];
                    case 1: return [2 /*return*/, _a.sent()];
                }
            });
        });
    };
    /**
     *
     */
    TypeRepository._set = function (mode, type, id, key, value) {
        return __awaiter(this, void 0, void 0, function () {
            var result, req, triggers, language, contents, requirements, script_type, packages, req;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!(mode === "a" && !value) /* null | undefined */) return [3 /*break*/, 2]; /* null | undefined */
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t            DELETE FROM LAMP_Aux.dbo.OOLAttachment\n\t            WHERE \n\t                ObjectID = '" + id + "'\n\t                AND [Key] = '" + key + "'\n\t                AND ObjectType = '" + type + "';\n\t\t\t")];
                    case 1:
                        /* DELETE */ result = _a.sent();
                        return [3 /*break*/, 8];
                    case 2:
                        if (!(mode === "a" && !!value) /* JSON value */) return [3 /*break*/, 4]; /* JSON value */
                        req = app_1.SQL.request();
                        req.input("json_value", mssql_1.default.NVarChar, JSON.stringify(value));
                        return [4 /*yield*/, req.query("\n\t            MERGE INTO LAMP_Aux.dbo.OOLAttachment\n\t                WITH (HOLDLOCK) AS Output\n\t            USING (SELECT\n\t                '" + type + "' AS ObjectType,\n\t                '" + id + "' AS ObjectID,\n\t                '" + key + "' AS [Key]\n\t            ) AS Input(ObjectType, ObjectID, [Key])\n\t            ON (\n\t                Output.[Key] = Input.[Key] \n\t                AND Output.ObjectID = Input.ObjectID \n\t                AND Output.ObjectType = Input.ObjectType \n\t            )\n\t            WHEN MATCHED THEN \n\t                UPDATE SET Value = @json_value\n\t            WHEN NOT MATCHED THEN \n\t                INSERT (\n\t                    ObjectType, ObjectID, [Key], Value\n\t                )\n\t                VALUES (\n\t                    '" + type + "', '" + id + "', '" + key + "', @json_value\n\t                );\n\t\t\t")];
                    case 3:
                        result = _a.sent();
                        return [3 /*break*/, 8];
                    case 4:
                        if (!(mode === "b" && !value) /* null | undefined */) return [3 /*break*/, 6]; /* null | undefined */
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t            DELETE FROM LAMP_Aux.dbo.OOLAttachmentLinker \n\t            WHERE \n\t                AttachmentKey = '" + key + "'\n\t                AND ObjectID = '" + id + "'\n\t                AND ChildObjectType = '" + type + "';\n\t\t\t")];
                    case 5:
                        /* DELETE */ result = _a.sent();
                        return [3 /*break*/, 8];
                    case 6:
                        if (!(mode === "b" && !!value) /* DynamicAttachment */) return [3 /*break*/, 8]; /* DynamicAttachment */
                        triggers = value.triggers, language = value.language, contents = value.contents, requirements = value.requirements;
                        script_type = JSON.stringify({ language: language, triggers: triggers });
                        packages = JSON.stringify(requirements) || "";
                        req = app_1.SQL.request();
                        req.input("script_contents", mssql_1.default.NVarChar, contents);
                        return [4 /*yield*/, req.query("\n\t            MERGE INTO LAMP_Aux.dbo.OOLAttachmentLinker \n\t                WITH (HOLDLOCK) AS Output\n\t            USING (SELECT\n\t                '" + key + "' AS AttachmentKey,\n\t                '" + id + "' AS ObjectID,\n\t                '" + type + "' AS ChildObjectType\n\t            ) AS Input(AttachmentKey, ObjectID, ChildObjectType)\n\t            ON (\n\t                Output.AttachmentKey = Input.AttachmentKey \n\t                AND Output.ObjectID = Input.ObjectID \n\t                AND Output.ChildObjectType = Input.ChildObjectType \n\t            )\n\t            WHEN MATCHED THEN \n\t                UPDATE SET \n\t                \tScriptType = '" + script_type + "',\n\t                \tScriptContents = @script_contents, \n\t                \tReqPackages = '" + packages + "'\n\t            WHEN NOT MATCHED THEN \n\t                INSERT (\n\t                    AttachmentKey, ObjectID, ChildObjectType, \n\t                    ScriptType, ScriptContents, ReqPackages\n\t                )\n\t                VALUES (\n\t                    '" + key + "', '" + id + "', '" + type + "',\n\t                    '" + script_type + "', @script_contents, '" + packages + "'\n\t                );\n\t\t\t")];
                    case 7:
                        result = _a.sent();
                        _a.label = 8;
                    case 8: return [2 /*return*/, result.rowsAffected[0] !== 0];
                }
            });
        });
    };
    /**
     * TODO: if key is undefined just return every item instead as an array
     */
    TypeRepository._get = function (mode, id, key) {
        return __awaiter(this, void 0, void 0, function () {
            var components, from_type, parents, result, result;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        components = Identifier_unpack(id);
                        from_type = components.length === 0 ? Participant_1.Participant.name : components[0];
                        return [4 /*yield*/, TypeRepository._parent(id)];
                    case 1:
                        parents = _a.sent();
                        if (Object.keys(parents).length === 0)
                            parents = { " ": " " }; // for the SQL 'IN' operator
                        if (!(mode === "a")) return [3 /*break*/, 3];
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t            SELECT TOP 1 * \n\t            FROM LAMP_Aux.dbo.OOLAttachment\n\t            WHERE [Key] = '" + key + "'\n\t                AND ((\n\t                \tObjectID = '" + id + "'\n\t                \tAND ObjectType = 'me'\n\t                ) OR (\n\t                \tObjectID IN (" + Object.values(parents)
                                .map(function (x) { return "'" + x + "'"; })
                                .join(", ") + ")\n\t                \tAND ObjectType IN ('" + from_type + "', '" + id + "')\n\t                ));\n\t\t\t")];
                    case 2:
                        result = (_a.sent()).recordset;
                        if (result.length === 0)
                            throw new Error("404.object-not-found");
                        return [2 /*return*/, JSON.parse(result[0].Value)];
                    case 3:
                        if (!(mode === "b")) return [3 /*break*/, 5];
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t            SELECT TOP 1 * \n\t            FROM LAMP_Aux.dbo.OOLAttachmentLinker\n\t            WHERE AttachmentKey = '" + key + "'\n\t            \tAND ((\n\t                \tObjectID = '" + id + "'\n\t                \tAND ChildObjectType = 'me'\n\t                ) OR (\n\t                \tObjectID IN (" + Object.values(parents)
                                .map(function (x) { return "'" + x + "'"; })
                                .join(", ") + ")\n\t                \tAND ChildObjectType IN ('" + from_type + "', '" + id + "')\n\t                ));\n\t\t\t")];
                    case 4:
                        result = (_a.sent()).recordset;
                        if (result.length === 0)
                            throw new Error("404.object-not-found");
                        // Convert all to DynamicAttachments.
                        return [2 /*return*/, result.map(function (x) {
                                var script_type = x.ScriptType.startsWith("{")
                                    ? JSON.parse(x.ScriptType)
                                    : { triggers: [], language: x.ScriptType };
                                var obj = new Type_1.DynamicAttachment();
                                obj.key = x.AttachmentKey;
                                obj.from = x.ObjectID;
                                obj.to = x.ChildObjectType;
                                obj.triggers = script_type.triggers;
                                obj.language = script_type.language;
                                obj.contents = x.ScriptContents;
                                obj.requirements = JSON.parse(x.ReqPackages);
                                return obj;
                            })[0]];
                    case 5: return [2 /*return*/];
                }
            });
        });
    };
    TypeRepository._list = function (mode, id) {
        return __awaiter(this, void 0, void 0, function () {
            var components, from_type, parents;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        components = Identifier_unpack(id);
                        from_type = components.length === 0 ? Participant_1.Participant.name : components[0];
                        return [4 /*yield*/, TypeRepository._parent(id)];
                    case 1:
                        parents = _a.sent();
                        if (Object.keys(parents).length === 0)
                            parents = { " ": " " }; // for the SQL 'IN' operator
                        if (!(mode === "a")) return [3 /*break*/, 3];
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t            SELECT [Key]\n\t            FROM LAMP_Aux.dbo.OOLAttachment\n\t            WHERE (\n\t                \tObjectID = '" + id + "'\n\t                \tAND ObjectType = 'me'\n\t                ) OR (\n\t                \tObjectID IN (" + Object.values(parents)
                                .map(function (x) { return "'" + x + "'"; })
                                .join(", ") + ")\n\t                \tAND ObjectType IN ('" + from_type + "', '" + id + "')\n\t                );\n\t\t\t")];
                    case 2: 
                    // Request all static attachments.
                    return [2 /*return*/, (_a.sent()).recordset.map(function (x) { return x.Key; })];
                    case 3: return [4 /*yield*/, app_1.SQL.request().query("\n\t            SELECT AttachmentKey\n\t            FROM LAMP_Aux.dbo.OOLAttachmentLinker\n\t            WHERE (\n\t                \tObjectID = '" + id + "'\n\t                \tAND ChildObjectType = 'me'\n\t                ) OR (\n\t                \tObjectID IN (" + Object.values(parents)
                            .map(function (x) { return "'" + x + "'"; })
                            .join(", ") + ")\n\t                \tAND ChildObjectType IN ('" + from_type + "', '" + id + "')\n\t                );\n\t\t\t")];
                    case 4: 
                    // Request all dynamic attachments.
                    return [2 /*return*/, (_a.sent()).recordset.map(function (x) { return x.AttachmentKey; })];
                }
            });
        });
    };
    /**
     *
     */
    TypeRepository._invoke = function (attachment, context) {
        return __awaiter(this, void 0, void 0, function () {
            var runner;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if ((attachment.contents || "").trim().length === 0)
                            return [2 /*return*/, undefined
                                // Select script runner for the right language...
                            ];
                        switch (attachment.language) {
                            case "rscript":
                                runner = new ScriptRunner_1.default.R();
                                break;
                            case "python":
                                runner = new ScriptRunner_1.default.Py();
                                break;
                            case "javascript":
                                runner = new ScriptRunner_1.default.JS();
                                break;
                            case "bash":
                                runner = new ScriptRunner_1.default.Bash();
                                break;
                            default:
                                throw new Error("400.invalid-script-runner");
                        }
                        return [4 /*yield*/, runner.execute(attachment.contents, attachment.requirements.join(","), context)];
                    case 1: 
                    // Execute script.
                    return [2 /*return*/, _a.sent()];
                }
            });
        });
    };
    /**
     *
     */
    TypeRepository._process_triggers = function () {
        return __awaiter(this, void 0, void 0, function () {
            var accumulated_set, ax_set1, ax_set2, registered_set, working_set, result;
            var _a;
            return __generator(this, function (_b) {
                switch (_b.label) {
                    case 0:
                        console.log("Processing accumulated attachment triggers...");
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tSELECT \n\t\t\t\tType, ID, Subtype, \n\t\t\t\tDATEDIFF_BIG(MS, '1970-01-01', LastUpdate) AS LastUpdate, \n\t\t\t\tUsers.StudyId AS _SID,\n\t\t\t\tUsers.AdminID AS _AID\n\t\t\tFROM LAMP_Aux.dbo.UpdateCounter\n\t\t\tLEFT JOIN LAMP.dbo.Users\n\t\t\t\tON Type = 'Participant' AND Users.UserID = ID\n\t\t\tORDER BY LastUpdate DESC;\n\t\t")];
                    case 1:
                        accumulated_set = (_b.sent()).recordset.map(function (x) { return (__assign(__assign({}, x), { _id: x.Type === "Participant"
                                ? ParticipantRepository_1.ParticipantRepository._pack_id({ study_id: app_1.Decrypt(x._SID) })
                                : ResearcherRepository_1.ResearcherRepository._pack_id({ admin_id: x.ID }), _admin: x.Type === "Participant"
                                ? ResearcherRepository_1.ResearcherRepository._pack_id({ admin_id: x._AID })
                                : ResearcherRepository_1.ResearcherRepository._pack_id({ admin_id: x.ID }) })); });
                        ax_set1 = accumulated_set.map(function (x) { return x._id; });
                        ax_set2 = accumulated_set.map(function (x) { return x._admin; });
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tSELECT * FROM LAMP_Aux.dbo.OOLAttachmentLinker; \n\t\t")];
                    case 2:
                        registered_set = (_b.sent()).recordset // TODO: SELECT * FROM LAMP_Aux.dbo.OOLTriggerSet;
                        ;
                        working_set = registered_set.filter(function (x) {
                            /* Attachment from self -> self. */
                            return (x.ChildObjectType === "me" && ax_set1.indexOf(x.ObjectID) >= 0) ||
                                /* Attachment from self -> children of type Participant */
                                (x.ChildObjectType === "Participant" && ax_set2.indexOf(x.ObjectID) >= 0) ||
                                /* Attachment from self -> specific child Participant matching an ID */
                                accumulated_set.find(function (y) { return y._id === x.ChildObjectType && y._admin === x.ObjectID; }) !== undefined;
                        });
                        return [4 /*yield*/, app_1.SQL.request().query("\n            DELETE FROM LAMP_Aux.dbo.UpdateCounter;\n\t\t")];
                    case 3:
                        result = _b.sent();
                        console.log("Resolved " + JSON.stringify(result.recordset) + " events.");
                        // Duplicate the working set into specific entries.
                        working_set = working_set
                            .map(function (x) {
                            var script_type = x.ScriptType.startsWith("{")
                                ? JSON.parse(x.ScriptType)
                                : { triggers: [], language: x.ScriptType };
                            var obj = new Type_1.DynamicAttachment();
                            obj.key = x.AttachmentKey;
                            obj.from = x.ObjectID;
                            obj.to = x.ChildObjectType;
                            obj.triggers = script_type.triggers;
                            obj.language = script_type.language;
                            obj.contents = x.ScriptContents;
                            obj.requirements = JSON.parse(x.ReqPackages);
                            return obj;
                        })
                            .map(function (x) {
                            // Apply a subgroup transformation only if we're targetting all
                            // child resources of a type (i.e. 'Participant').
                            if (x.to === "Participant")
                                return accumulated_set
                                    .filter(function (y) { return y.Type === "Participant" && y._admin === x.from && y._id !== y._admin; })
                                    .map(function (y) { return (__assign(__assign({}, x), { to: y._id })); });
                            return [__assign(__assign({}, x), { to: x.from })];
                        });
                        (_a = []).concat.apply(_a, __spread(working_set)).forEach(function (x) {
                            return TypeRepository._invoke(x, {
                                /* The security context originator for the script
                                           with a magic placeholder to indicate to the LAMP server
                                           that the script's API requests are pre-authenticated. */
                                token: "LAMP" +
                                    app_1.Encrypt(JSON.stringify({
                                        identity: { from: x.from, to: x.to },
                                        cosigner: app_1.Root,
                                    })),
                                /* What object was this automation run for on behalf of an agent? */
                                object: {
                                    id: x.to,
                                    type: TypeRepository._self_type(x.to),
                                },
                                /* Currently meaningless but does signify what caused the IA to run. */
                                event: ["ActivityEvent", "SensorEvent"],
                            })
                                .then(function (y) {
                                TypeRepository._set("a", x.to, x.from, x.key + "/output", y);
                            })
                                .catch(function (err) {
                                TypeRepository._set("a", x.to, x.from, x.key + "/output", JSON.stringify({ output: null, logs: err }));
                            });
                        });
                        return [2 /*return*/];
                }
            });
        });
    };
    return TypeRepository;
}());
exports.TypeRepository = TypeRepository;
/**
 * Set up a 5-minute interval callback to invoke triggers.
 */
/*setInterval(() => {
  if (!!SQL) TypeRepository._process_triggers()
}, 5 * 60 * 1000)*/
// TODO: below is to convert legacy scheduling into modern cron-like versions
/* FIXME:
$obj->schedule = isset($raw->schedule) ? array_merge(...array_map(function($x) {
    $duration = new DurationInterval(); $ri = $x->repeat_interval;
    if ($ri >= 0 && $ri <= 4) { // hourly
        $h = ($ri == 4 ? 12 : ($ri == 3 ? 6 : ($ri == 2 ? 3 : 1)));
        $duration->interval = new CalendarComponents();
        $duration->interval->hour = $h;
    } else if ($ri >= 5 && $ri <= 10) { // weekly+
        if ($ri == 6) {
            $duration = [
                new DurationInterval(strtotime($x->time) * 1000, new CalendarComponents()),
                new DurationInterval(strtotime($x->time) * 1000, new CalendarComponents())
            ];
            $duration[0]->interval->weekday = 2;
            $duration[1]->interval->weekday = 4;
        } else if ($ri == 7) {
            $duration = [
                new DurationInterval(strtotime($x->time) * 1000, new CalendarComponents()),
                new DurationInterval(strtotime($x->time) * 1000, new CalendarComponents()),
                new DurationInterval(strtotime($x->time) * 1000, new CalendarComponents())
            ];
            $duration[0]->interval->weekday = 1;
            $duration[1]->interval->weekday = 3;
            $duration[2]->interval->weekday = 5;
        } else {
            $duration = [
                new DurationInterval(strtotime($x->time) * 1000, new CalendarComponents())
            ];
            $duration[0]->interval->day = ($ri == 5 ? 1 : null);
            $duration[0]->interval->week_of_month = ($ri == 9 ? 2 : ($ri == 8 ? 1 : null));
            $duration[0]->interval->month = ($ri == 10 ? 1 : null);
        }
    } else if ($ri == 11 && count($x->custom_time) == 1) { // custom+
        $duration->start = strtotime($x->custom_time[0]) * 1000;
        $duration->repeat_count = 1;
    } else if ($ri == 11 && count($x->custom_time) > 2) { // custom*
        $int_comp = (new DateTime($x->custom_time[0]))
                        ->diff(new DateTime($x->custom_time[1]));
        $duration->start = strtotime($x->custom_time[0]) * 1000;
        $duration->interval = new CalendarComponents();
        $duration->interval->year = ($int_comp->y == 0 ? null : $int_comp->y);
        $duration->interval->month = ($int_comp->m == 0 ? null : $int_comp->m);
        $duration->interval->day = ($int_comp->d == 0 ? null : $int_comp->d);
        $duration->interval->hour = ($int_comp->h == 0 ? null : $int_comp->h);
        $duration->interval->minute = ($int_comp->i == 0 ? null : $int_comp->i);
        $duration->interval->second = ($int_comp->s == 0 ? null : $int_comp->s);
        $duration->repeat_count = count($x->custom_time) - 1;
    } else if ($ri == 12) { // none
        $duration->start = strtotime($x->time) * 1000;
        $duration->repeat_count = 1;
    }
    return is_array($duration) ? $duration : [$duration];
}, $raw->schedule)) : null;
*/
// Schedule:
//      - Admin_CTestSchedule, Admin_SurveySchedule
//          - AdminID, CTestID/SurveyID, Version*(C), ScheduleDate, SlotID, Time, RepeatID, IsDeleted
//      - Admin_CTestScheduleCustomTime, Admin_SurveyScheduleCustomTime, Admin_BatchScheduleCustomTime
//          - Time
//      - Admin_BatchSchedule
//          - AdminID, BatchName, ScheduleDate, SlotID, Time, RepeatID, IsDeleted
//      - Admin_BatchScheduleCTest, Admin_BatchScheduleSurvey
//          - CTestID/SurveyID, Version*(C), Order
//
// Settings:
//      - Admin_CTestSurveySettings
//          - AdminID, CTestID, SurveyID
//      - Admin_JewelsTrailsASettings, Admin_JewelsTrailsBSettings
//          - AdminID, ... (")
//      - SurveyQuestions
//          - SurveyID, QuestionText, AnswerType, IsDeleted
//      - SurveyQuestionsOptions
//          - QuestionID, OptionText
// https://en.wikipedia.org/wiki/Cron#CRON_expression
/*
* * * * * *
| | | | | |
| | | | | +-- Year              (range: 1900-3000)
| | | | +---- Day of the Week   (range: 1-7; L=last, #=ordinal(range: 1-4))
| | | +------ Month of the Year (range: 1-12)
| | +-------- Day of the Month  (range: 1-31; L=last, W=nearest-weekday, #=ordinal(range: 1-52))
| +---------- Hour              (range: 0-23)
+------------ Minute            (range: 0-59)
*/
/*



enum RepeatTypeLegacy {
    hourly = 'hourly', // 0 * * * * *
    every3h = 'every3h', // 0 * /3 * * * *
    every6h = 'every6h', // 0 * /6 * * * *
    every12h = 'every12h', // 0 * /12 * * * *
    daily = 'daily', // 0 0 * * * *
    weekly = 'weekly', // 0 0 * * 0 *
    biweekly = 'biweekly', // 0 0 1,15 * * *
    monthly = 'monthly', // 0 0 1 * * *
    bimonthly = 'bimonthly', // 0 0 1 * /2 * *
    custom = 'custom', // 1 2 3 4 5 6
    none = 'none' // 0 0 0 0 0 0
}


*/
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiVHlwZVJlcG9zaXRvcnkuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi9zcmMvcmVwb3NpdG9yeS9UeXBlUmVwb3NpdG9yeS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUFBLDhCQUFvRDtBQUNwRCx1RUFBZ0Q7QUFDaEQsZ0RBQXVCO0FBQ3ZCLG9EQUFrRDtBQUlsRCxzQ0FBaUQ7QUFDakQsMkVBQXlFO0FBQ3pFLDZFQUEyRTtBQUMzRSxpRUFBK0Q7QUFDL0QsdUVBQXFFO0FBRXJFLFNBQWdCLGVBQWUsQ0FBQyxVQUFpQjtJQUMvQyxJQUFJLFVBQVUsQ0FBQyxNQUFNLEtBQUssQ0FBQztRQUFFLE9BQU8sRUFBRSxDQUFBO0lBQ3RDLE9BQU8sTUFBTSxDQUFDLElBQUksQ0FBQyxVQUFVLENBQUMsSUFBSSxDQUFDLEdBQUcsQ0FBQyxDQUFDLENBQUMsUUFBUSxDQUFDLFFBQVEsQ0FBQyxDQUFDLE9BQU8sQ0FBQyxJQUFJLEVBQUUsR0FBRyxDQUFDLENBQUE7QUFDaEYsQ0FBQztBQUhELDBDQUdDO0FBQ0QsU0FBZ0IsaUJBQWlCLENBQUMsVUFBa0I7SUFDbEQsSUFBSSxVQUFVLENBQUMsS0FBSyxDQUFDLFVBQVUsQ0FBQztRQUFFLE9BQU8sRUFBRSxDQUFBO0lBQzNDLE9BQU8sTUFBTSxDQUFDLElBQUksQ0FBVSxVQUFXLENBQUMsT0FBTyxDQUFDLElBQUksRUFBRSxHQUFHLENBQUMsRUFBRSxRQUFRLENBQUM7U0FDbEUsUUFBUSxDQUFDLE1BQU0sQ0FBQztTQUNoQixLQUFLLENBQUMsR0FBRyxDQUFDLENBQUE7QUFDZixDQUFDO0FBTEQsOENBS0M7QUFFRDtJQUFBO0lBNlpBLENBQUM7SUE1WnFCLHNCQUFPLEdBQTNCLFVBQTRCLE9BQWU7Ozs7Ozs7d0JBQ25DLE1BQU0sR0FBUSxFQUFF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