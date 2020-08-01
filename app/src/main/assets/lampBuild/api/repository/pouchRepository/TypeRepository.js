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
var app_1 = require("../../app");
var ScriptRunner_1 = __importDefault(require("../../utils/ScriptRunner"));
var mssql_1 = __importDefault(require("mssql"));
var Participant_1 = require("../../model/Participant");
var Type_1 = require("../../model/Type");
var ResearcherRepository_1 = require("../../repository/ResearcherRepository");
var ParticipantRepository_1 = require("../../repository/ParticipantRepository");
var StudyRepository_1 = require("../../repository/StudyRepository");
var ActivityRepository_1 = require("../../repository/ActivityRepository");
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiVHlwZVJlcG9zaXRvcnkuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi9zcmMvcmVwb3NpdG9yeS9wb3VjaFJlcG9zaXRvcnkvVHlwZVJlcG9zaXRvcnkudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7QUFBQSxpQ0FBdUQ7QUFDdkQsMEVBQW1EO0FBQ25ELGdEQUF1QjtBQUN2Qix1REFBcUQ7QUFJckQseUNBQW9EO0FBQ3BELDhFQUE0RTtBQUM1RSxnRkFBOEU7QUFDOUUsb0VBQWtFO0FBQ2xFLDBFQUF3RTtBQUV4RSxTQUFnQixlQUFlLENBQUMsVUFBaUI7SUFDL0MsSUFBSSxVQUFVLENBQUMsTUFBTSxLQUFLLENBQUM7UUFBRSxPQUFPLEVBQUUsQ0FBQTtJQUN0QyxPQUFPLE1BQU0sQ0FBQyxJQUFJLENBQUMsVUFBVSxDQUFDLElBQUksQ0FBQyxHQUFHLENBQUMsQ0FBQyxDQUFDLFFBQVEsQ0FBQyxRQUFRLENBQUMsQ0FBQyxPQUFPLENBQUMsSUFBSSxFQUFFLEdBQUcsQ0FBQyxDQUFBO0FBQ2hGLENBQUM7QUFIRCwwQ0FHQztBQUNELFNBQWdCLGlCQUFpQixDQUFDLFVBQWtCO0lBQ2xELElBQUksVUFBVSxDQUFDLEtBQUssQ0FBQyxVQUFVLENBQUM7UUFBRSxPQUFPLEVBQUUsQ0FBQTtJQUMzQyxPQUFPLE1BQU0sQ0FBQyxJQUFJLENBQVUsVUFBVyxDQUFDLE9BQU8sQ0FBQyxJQUFJLEVBQUUsR0FBRyxDQUFDLEVBQUUsUUFBUSxDQUFDO1NBQ2xFLFFBQVEsQ0FBQyxNQUFNLENBQUM7U0FDaEIsS0FBSyxDQUFDLEdBQUcsQ0FBQyxDQUFBO0FBQ2YsQ0FBQztBQUxELDhDQUtDO0FBRUQ7SUFBQTtJQTZaQSxDQUFDO0lBNVpxQixzQkFBTyxHQUEzQixVQUE0QixPQUFlOzs7Ozs7O3dCQUNuQyxNQUFNLEdBQVEsRUFBRSxDQUFDLDJEQUEyRDt3QkFBNUQsQ0FBQTs7Ozt3QkFDSSxLQUFBLFNBQUEsY0FBYyxDQUFDLFlBQVksQ0FBQyxPQUFPLENBQUMsQ0FBQTs7Ozt3QkFBbkQsV0FBVzt3QkFDcEIsS0FBQSxNQUFNLENBQUE7d0JBQUMsS0FBQSxXQUFXLENBQUE7d0JBQUkscUJBQU0sY0FBYyxDQUFDLFVBQVUsQ0FBQyxPQUFPLEVBQUUsV0FBVyxDQUFDLEVBQUE7O3dCQUEzRSxNQUFtQixHQUFHLFNBQXFELENBQUE7Ozs7Ozs7Ozs7Ozs7Ozs7NEJBQzdFLHNCQUFPLE1BQU0sRUFBQTs7OztLQUNkO0lBRUQ7O09BRUc7SUFDVyx5QkFBVSxHQUF4QixVQUF5QixPQUFlO1FBQ3RDLElBQU0sVUFBVSxHQUFHLGlCQUFpQixDQUFDLE9BQU8sQ0FBQyxDQUFBO1FBQzdDLElBQU0sU0FBUyxHQUFXLFVBQVUsQ0FBQyxNQUFNLEtBQUssQ0FBQyxDQUFDLENBQUMsQ0FBTyx5QkFBWSxDQUFDLElBQUksQ0FBQyxDQUFDLENBQUMsVUFBVSxDQUFDLENBQUMsQ0FBQyxDQUFBO1FBQzNGLE9BQU8sU0FBUyxDQUFBO0lBQ2xCLENBQUM7SUFFRDs7T0FFRztJQUNXLDJCQUFZLEdBQTFCLFVBQTJCLE9BQWU7UUFDeEMsSUFBTSxZQUFZLEdBQWlDO1lBQ2pELFVBQVUsRUFBRSxFQUFFO1lBQ2QsS0FBSyxFQUFFLENBQUMsWUFBWSxDQUFDO1lBQ3JCLFdBQVcsRUFBRSxDQUFDLE9BQU8sRUFBRSxZQUFZLENBQUM7WUFDcEMsUUFBUSxFQUFFLENBQUMsT0FBTyxFQUFFLFlBQVksQ0FBQztTQUNsQyxDQUFBO1FBQ0Q7Ozs7Ozs7Ozs7OztjQVlBO1FBQ0EsT0FBTyxZQUFZLENBQUMsY0FBYyxDQUFDLFVBQVUsQ0FBQyxPQUFPLENBQUMsQ0FBQyxDQUFBO0lBQ3pELENBQUM7SUFFRDs7T0FFRztJQUNpQix5QkFBVSxHQUE5QixVQUErQixPQUFlLEVBQUUsSUFBWTs7Ozs7O3dCQUNwRCxTQUFTLEdBQWlDOzRCQUM5QyxVQUFVLEVBQUUsMkNBQW9COzRCQUNoQyxLQUFLLEVBQUUsaUNBQWU7NEJBQ3RCLFdBQVcsRUFBRSw2Q0FBcUI7NEJBQ2xDLFFBQVEsRUFBRSx1Q0FBa0I7eUJBQzdCLENBQUE7d0JBQ00scUJBQVksU0FBUyxDQUFDLGNBQWMsQ0FBQyxVQUFVLENBQUMsT0FBTyxDQUFDLENBQUUsQ0FBQyxVQUFVLENBQUMsT0FBTyxFQUFFLFNBQVMsQ0FBQyxJQUFJLENBQUMsQ0FBQyxFQUFBOzRCQUF0RyxzQkFBTyxTQUErRixFQUFBOzs7O0tBQ3ZHO0lBRUQ7O09BRUc7SUFDaUIsbUJBQUksR0FBeEIsVUFBeUIsSUFBZSxFQUFFLElBQVksRUFBRSxFQUFVLEVBQUUsR0FBVyxFQUFFLEtBQStCOzs7Ozs7NkJBRTFHLENBQUEsSUFBSSxLQUFLLEdBQUcsSUFBSSxDQUFDLEtBQUssQ0FBQSxDQUFDLHNCQUFzQixFQUE3Qyx3QkFBc0IsQ0FBQyxzQkFBc0I7d0JBQ3pCLHFCQUFNLFNBQUksQ0FBQyxPQUFPLEVBQUUsQ0FBQyxLQUFLLENBQUMsaUhBR3hCLEVBQUUsMENBQ0QsR0FBRywrQ0FDRSxJQUFJLGVBQ3JDLENBQUMsRUFBQTs7d0JBTkMsWUFBWSxDQUFDLE1BQU0sR0FBRyxTQU12QixDQUFBOzs7NkJBQ1UsQ0FBQSxJQUFJLEtBQUssR0FBRyxJQUFJLENBQUMsQ0FBQyxLQUFLLENBQUEsQ0FBQyxnQkFBZ0IsRUFBeEMsd0JBQXVCLENBQUMsZ0JBQWdCO3dCQUNwQixHQUFHLEdBQUcsU0FBSSxDQUFDLE9BQU8sRUFBRSxDQUFBO3dCQUNqRCxHQUFHLENBQUMsS0FBSyxDQUFDLFlBQVksRUFBRSxlQUFHLENBQUMsUUFBUSxFQUFFLElBQUksQ0FBQyxTQUFTLENBQUMsS0FBSyxDQUFDLENBQUMsQ0FBQTt3QkFDbkQscUJBQU0sR0FBRyxDQUFDLEtBQUssQ0FBQyx5SkFJWCxJQUFJLDZDQUNKLEVBQUUsMkNBQ0YsR0FBRyw2aUJBY0MsSUFBSSxZQUFPLEVBQUUsWUFBTyxHQUFHLGlEQUUzQyxDQUFDLEVBQUE7O3dCQXRCQyxNQUFNLEdBQUcsU0FzQlYsQ0FBQTs7OzZCQUNVLENBQUEsSUFBSSxLQUFLLEdBQUcsSUFBSSxDQUFDLEtBQUssQ0FBQSxDQUFDLHNCQUFzQixFQUE3Qyx3QkFBc0IsQ0FBQyxzQkFBc0I7d0JBQ2hDLHFCQUFNLFNBQUksQ0FBQyxPQUFPLEVBQUUsQ0FBQyxLQUFLLENBQUMsNkhBR25CLEdBQUcsNkNBQ0osRUFBRSxvREFDSyxJQUFJLGVBQzFDLENBQUMsRUFBQTs7d0JBTkMsWUFBWSxDQUFDLE1BQU0sR0FBRyxTQU12QixDQUFBOzs7NkJBQ1UsQ0FBQSxJQUFJLEtBQUssR0FBRyxJQUFJLENBQUMsQ0FBQyxLQUFLLENBQUEsQ0FBQyx1QkFBdUIsRUFBL0Msd0JBQXVCLENBQUMsdUJBQXVCO3dCQUN6QixRQUFRLEdBQXVDLEtBQUssU0FBNUMsRUFBRSxRQUFRLEdBQTZCLEtBQUssU0FBbEMsRUFBRSxRQUFRLEdBQW1CLEtBQUssU0FBeEIsRUFBRSxZQUFZLEdBQUssS0FBSyxhQUFWLENBQVU7d0JBQzdFLFdBQVcsR0FBRyxJQUFJLENBQUMsU0FBUyxDQUFDLEVBQUUsUUFBUSxVQUFBLEVBQUUsUUFBUSxVQUFBLEVBQUUsQ0FBQyxDQUFBO3dCQUNwRCxRQUFRLEdBQUcsSUFBSSxDQUFDLFNBQVMsQ0FBQyxZQUFZLENBQUMsSUFBSSxFQUFFLENBQUE7d0JBRTdDLEdBQUcsR0FBRyxTQUFJLENBQUMsT0FBTyxFQUFFLENBQUE7d0JBQzFCLEdBQUcsQ0FBQyxLQUFLLENBQUMsaUJBQWlCLEVBQUUsZUFBRyxDQUFDLFFBQVEsRUFBRSxRQUFRLENBQUMsQ0FBQTt3QkFDM0MscUJBQU0sR0FBRyxDQUFDLEtBQUssQ0FBQyxnS0FJWCxHQUFHLGdEQUNILEVBQUUsMkNBQ0YsSUFBSSx5YUFTVSxXQUFXLHdHQUVWLFFBQVEsc1JBT25CLEdBQUcsWUFBTyxFQUFFLFlBQU8sSUFBSSxtQ0FDdkIsV0FBVyw4QkFBeUIsUUFBUSxvQ0FFaEUsQ0FBQyxFQUFBOzt3QkEzQkMsTUFBTSxHQUFHLFNBMkJWLENBQUE7OzRCQUVELHNCQUFPLE1BQU8sQ0FBQyxZQUFZLENBQUMsQ0FBQyxDQUFDLEtBQUssQ0FBQyxFQUFBOzs7O0tBQ3JDO0lBRUQ7O09BRUc7SUFDaUIsbUJBQUksR0FBeEIsVUFBeUIsSUFBZSxFQUFFLEVBQVUsRUFBRSxHQUFXOzs7Ozs7d0JBQ3pELFVBQVUsR0FBRyxpQkFBaUIsQ0FBQyxFQUFFLENBQUMsQ0FBQTt3QkFDbEMsU0FBUyxHQUFXLFVBQVUsQ0FBQyxNQUFNLEtBQUssQ0FBQyxDQUFDLENBQUMsQ0FBTyx5QkFBWSxDQUFDLElBQUksQ0FBQyxDQUFDLENBQUMsVUFBVSxDQUFDLENBQUMsQ0FBQyxDQUFBO3dCQUM3RSxxQkFBTSxjQUFjLENBQUMsT0FBTyxDQUFTLEVBQUUsQ0FBQyxFQUFBOzt3QkFBbEQsT0FBTyxHQUFHLFNBQXdDO3dCQUN0RCxJQUFJLE1BQU0sQ0FBQyxJQUFJLENBQUMsT0FBTyxDQUFDLENBQUMsTUFBTSxLQUFLLENBQUM7NEJBQUUsT0FBTyxHQUFHLEVBQUUsR0FBRyxFQUFFLEdBQUcsRUFBRSxDQUFBLENBQUMsNEJBQTRCOzZCQUV0RixDQUFBLElBQUksS0FBSyxHQUFHLENBQUEsRUFBWix3QkFBWTt3QkFFWixxQkFBTSxTQUFJLENBQUMsT0FBTyxFQUFFLENBQUMsS0FBSyxDQUFDLGtIQUdMLEdBQUcscUVBRUQsRUFBRSxpSEFHRCxNQUFNLENBQUMsTUFBTSxDQUFDLE9BQU8sQ0FBQztpQ0FDaEMsR0FBRyxDQUFDLFVBQUMsQ0FBQyxJQUFLLE9BQUEsTUFBSSxDQUFDLE1BQUcsRUFBUixDQUFRLENBQUM7aUNBQ3BCLElBQUksQ0FBQyxJQUFJLENBQUMsbURBQ08sU0FBUyxZQUFPLEVBQUUsc0NBRXRELENBQUMsRUFBQTs7d0JBZE8sTUFBTSxHQUFHLENBQ2IsU0FhSCxDQUNFLENBQUMsU0FBUzt3QkFFWCxJQUFJLE1BQU0sQ0FBQyxNQUFNLEtBQUssQ0FBQzs0QkFBRSxNQUFNLElBQUksS0FBSyxDQUFDLHNCQUFzQixDQUFDLENBQUE7d0JBQ2hFLHNCQUFPLElBQUksQ0FBQyxLQUFLLENBQUMsTUFBTSxDQUFDLENBQUMsQ0FBQyxDQUFDLEtBQUssQ0FBQyxFQUFBOzs2QkFDekIsQ0FBQSxJQUFJLEtBQUssR0FBRyxDQUFBLEVBQVosd0JBQVk7d0JBRW5CLHFCQUFNLFNBQUksQ0FBQyxPQUFPLEVBQUUsQ0FBQyxLQUFLLENBQUMsZ0lBR0csR0FBRyxtRUFFVCxFQUFFLHNIQUdELE1BQU0sQ0FBQyxNQUFNLENBQUMsT0FBTyxDQUFDO2lDQUNoQyxHQUFHLENBQUMsVUFBQyxDQUFDLElBQUssT0FBQSxNQUFJLENBQUMsTUFBRyxFQUFSLENBQVEsQ0FBQztpQ0FDcEIsSUFBSSxDQUFDLElBQUksQ0FBQyx3REFDWSxTQUFTLFlBQU8sRUFBRSxzQ0FFM0QsQ0FBQyxFQUFBOzt3QkFkTyxNQUFNLEdBQUcsQ0FDYixTQWFILENBQ0UsQ0FBQyxTQUFTO3dCQUNYLElBQUksTUFBTSxDQUFDLE1BQU0sS0FBSyxDQUFDOzRCQUFFLE1BQU0sSUFBSSxLQUFLLENBQUMsc0JBQXNCLENBQUMsQ0FBQTt3QkFFaEUscUNBQXFDO3dCQUNyQyxzQkFBTyxNQUFNLENBQUMsR0FBRyxDQUFDLFVBQUMsQ0FBQztnQ0FDbEIsSUFBTSxXQUFXLEdBQUcsQ0FBQyxDQUFDLFVBQVUsQ0FBQyxVQUFVLENBQUMsR0FBRyxDQUFDO29DQUM5QyxDQUFDLENBQUMsSUFBSSxDQUFDLEtBQUssQ0FBQyxDQUFDLENBQUMsVUFBVSxDQUFDO29DQUMxQixDQUFDLENBQUMsRUFBRSxRQUFRLEVBQUUsRUFBRSxFQUFFLFFBQVEsRUFBRSxDQUFDLENBQUMsVUFBVSxFQUFFLENBQUE7Z0NBRTVDLElBQU0sR0FBRyxHQUFHLElBQUksd0JBQWlCLEVBQUUsQ0FBQTtnQ0FDbkMsR0FBRyxDQUFDLEdBQUcsR0FBRyxDQUFDLENBQUMsYUFBYSxDQUFBO2dDQUN6QixHQUFHLENBQUMsSUFBSSxHQUFHLENBQUMsQ0FBQyxRQUFRLENBQUE7Z0NBQ3JCLEdBQUcsQ0FBQyxFQUFFLEdBQUcsQ0FBQyxDQUFDLGVBQWUsQ0FBQTtnQ0FDMUIsR0FBRyxDQUFDLFFBQVEsR0FBRyxXQUFXLENBQUMsUUFBUSxDQUFBO2dDQUNuQyxHQUFHLENBQUMsUUFBUSxHQUFHLFdBQVcsQ0FBQyxRQUFRLENBQUE7Z0NBQ25DLEdBQUcsQ0FBQyxRQUFRLEdBQUcsQ0FBQyxDQUFDLGNBQWMsQ0FBQTtnQ0FDL0IsR0FBRyxDQUFDLFlBQVksR0FBRyxJQUFJLENBQUMsS0FBSyxDQUFDLENBQUMsQ0FBQyxXQUFXLENBQUMsQ0FBQTtnQ0FDNUMsT0FBTyxHQUFHLENBQUE7NEJBQ1osQ0FBQyxDQUFDLENBQUMsQ0FBQyxDQUFDLEVBQUE7Ozs7O0tBRVI7SUFFbUIsb0JBQUssR0FBekIsVUFBMEIsSUFBZSxFQUFFLEVBQVU7Ozs7Ozt3QkFFN0MsVUFBVSxHQUFHLGlCQUFpQixDQUFDLEVBQUUsQ0FBQyxDQUFBO3dCQUNsQyxTQUFTLEdBQVcsVUFBVSxDQUFDLE1BQU0sS0FBSyxDQUFDLENBQUMsQ0FBQyxDQUFPLHlCQUFZLENBQUMsSUFBSSxDQUFDLENBQUMsQ0FBQyxVQUFVLENBQUMsQ0FBQyxDQUFDLENBQUE7d0JBQzdFLHFCQUFNLGNBQWMsQ0FBQyxPQUFPLENBQVMsRUFBRSxDQUFDLEVBQUE7O3dCQUFsRCxPQUFPLEdBQUcsU0FBd0M7d0JBQ3RELElBQUksTUFBTSxDQUFDLElBQUksQ0FBQyxPQUFPLENBQUMsQ0FBQyxNQUFNLEtBQUssQ0FBQzs0QkFBRSxPQUFPLEdBQUcsRUFBRSxHQUFHLEVBQUUsR0FBRyxFQUFFLENBQUEsQ0FBQyw0QkFBNEI7NkJBRXRGLENBQUEsSUFBSSxLQUFLLEdBQUcsQ0FBQSxFQUFaLHdCQUFZO3dCQUdaLHFCQUFNLFNBQUksQ0FBQyxPQUFPLEVBQUUsQ0FBQyxLQUFLLENBQUMseUlBSUgsRUFBRSxpSEFHRCxNQUFNLENBQUMsTUFBTSxDQUFDLE9BQU8sQ0FBQztpQ0FDaEMsR0FBRyxDQUFDLFVBQUMsQ0FBQyxJQUFLLE9BQUEsTUFBSSxDQUFDLE1BQUcsRUFBUixDQUFRLENBQUM7aUNBQ3BCLElBQUksQ0FBQyxJQUFJLENBQUMsbURBQ08sU0FBUyxZQUFPLEVBQUUscUNBRXRELENBQUMsRUFBQTs7b0JBZEMsa0NBQWtDO29CQUNsQyxzQkFBTyxDQUNMLFNBWUgsQ0FDRSxDQUFDLFNBQVMsQ0FBQyxHQUFHLENBQUMsVUFBQyxDQUFDLElBQUssT0FBQSxDQUFDLENBQUMsR0FBRyxFQUFMLENBQUssQ0FBQyxFQUFBOzRCQUkzQixxQkFBTSxTQUFJLENBQUMsT0FBTyxFQUFFLENBQUMsS0FBSyxDQUFDLHVKQUlILEVBQUUsc0hBR0QsTUFBTSxDQUFDLE1BQU0sQ0FBQyxPQUFPLENBQUM7NkJBQ2hDLEdBQUcsQ0FBQyxVQUFDLENBQUMsSUFBSyxPQUFBLE1BQUksQ0FBQyxNQUFHLEVBQVIsQ0FBUSxDQUFDOzZCQUNwQixJQUFJLENBQUMsSUFBSSxDQUFDLHdEQUNZLFNBQVMsWUFBTyxFQUFFLHFDQUUzRCxDQUFDLEVBQUE7O29CQWRDLG1DQUFtQztvQkFDbkMsc0JBQU8sQ0FDTCxTQVlILENBQ0UsQ0FBQyxTQUFTLENBQUMsR0FBRyxDQUFDLFVBQUMsQ0FBQyxJQUFLLE9BQUEsQ0FBQyxDQUFDLGFBQWEsRUFBZixDQUFlLENBQUMsRUFBQTs7OztLQUUxQztJQUVEOztPQUVHO0lBQ2lCLHNCQUFPLEdBQTNCLFVBQTRCLFVBQTZCLEVBQUUsT0FBWTs7Ozs7O3dCQUNyRSxJQUFJLENBQUMsVUFBVSxDQUFDLFFBQVEsSUFBSSxFQUFFLENBQUMsQ0FBQyxJQUFJLEVBQUUsQ0FBQyxNQUFNLEtBQUssQ0FBQzs0QkFBRSxzQkFBTyxTQUFTO2dDQUVyRSxpREFBaUQ7OEJBRm9CO3dCQUlyRSxRQUFRLFVBQVUsQ0FBQyxRQUFRLEVBQUU7NEJBQzNCLEtBQUssU0FBUztnQ0FDWixNQUFNLEdBQUcsSUFBSSxzQkFBWSxDQUFDLENBQUMsRUFBRSxDQUFBO2dDQUM3QixNQUFLOzRCQUNQLEtBQUssUUFBUTtnQ0FDWCxNQUFNLEdBQUcsSUFBSSxzQkFBWSxDQUFDLEVBQUUsRUFBRSxDQUFBO2dDQUM5QixNQUFLOzRCQUNQLEtBQUssWUFBWTtnQ0FDZixNQUFNLEdBQUcsSUFBSSxzQkFBWSxDQUFDLEVBQUUsRUFBRSxDQUFBO2dDQUM5QixNQUFLOzRCQUNQLEtBQUssTUFBTTtnQ0FDVCxNQUFNLEdBQUcsSUFBSSxzQkFBWSxDQUFDLElBQUksRUFBRSxDQUFBO2dDQUNoQyxNQUFLOzRCQUNQO2dDQUNFLE1BQU0sSUFBSSxLQUFLLENBQUMsMkJBQTJCLENBQUMsQ0FBQTt5QkFDL0M7d0JBR00scUJBQU0sTUFBTSxDQUFDLE9BQU8sQ0FBQyxVQUFVLENBQUMsUUFBUyxFQUFFLFVBQVUsQ0FBQyxZQUFhLENBQUMsSUFBSSxDQUFDLEdBQUcsQ0FBQyxFQUFFLE9BQU8sQ0FBQyxFQUFBOztvQkFEOUYsa0JBQWtCO29CQUNsQixzQkFBTyxTQUF1RixFQUFBOzs7O0tBQy9GO0lBRUQ7O09BRUc7SUFDaUIsZ0NBQWlCLEdBQXJDOzs7Ozs7O3dCQUNFLE9BQU8sQ0FBQyxHQUFHLENBQUMsK0NBQStDLENBQUMsQ0FBQTt3QkFJMUQscUJBQU0sU0FBSSxDQUFDLE9BQU8sRUFBRSxDQUFDLEtBQUssQ0FBQyxzVkFVOUIsQ0FBQyxFQUFBOzt3QkFYTSxlQUFlLEdBQUcsQ0FDdEIsU0FVRixDQUNDLENBQUMsU0FBUyxDQUFDLEdBQUcsQ0FBQyxVQUFDLENBQUMsSUFBSyxPQUFBLHVCQUNsQixDQUFDLEtBQ0osR0FBRyxFQUNELENBQUMsQ0FBQyxJQUFJLEtBQUssYUFBYTtnQ0FDdEIsQ0FBQyxDQUFDLDZDQUFxQixDQUFDLFFBQVEsQ0FBQyxFQUFFLFFBQVEsRUFBRSxhQUFPLENBQVMsQ0FBQyxDQUFDLElBQUksQ0FBQyxFQUFFLENBQUM7Z0NBQ3ZFLENBQUMsQ0FBQywyQ0FBb0IsQ0FBQyxRQUFRLENBQUMsRUFBRSxRQUFRLEVBQUUsQ0FBQyxDQUFDLEVBQUUsRUFBRSxDQUFDLEVBQ3ZELE1BQU0sRUFDSixDQUFDLENBQUMsSUFBSSxLQUFLLGFBQWE7Z0NBQ3RCLENBQUMsQ0FBQywyQ0FBb0IsQ0FBQyxRQUFRLENBQUMsRUFBRSxRQUFRLEVBQUUsQ0FBQyxDQUFDLElBQUksRUFBRSxDQUFDO2dDQUNyRCxDQUFDLENBQUMsMkNBQW9CLENBQUMsUUFBUSxDQUFDLEVBQUUsUUFBUSxFQUFFLENBQUMsQ0FBQyxFQUFFLEVBQUUsQ0FBQyxJQUN2RCxFQVZxQixDQVVyQixDQUFDO3dCQUNHLE9BQU8sR0FBRyxlQUFlLENBQUMsR0FBRyxDQUFDLFVBQUMsQ0FBQyxJQUFLLE9BQUEsQ0FBQyxDQUFDLEdBQUcsRUFBTCxDQUFLLENBQUMsQ0FBQTt3QkFDM0MsT0FBTyxHQUFHLGVBQWUsQ0FBQyxHQUFHLENBQUMsVUFBQyxDQUFDLElBQUssT0FBQSxDQUFDLENBQUMsTUFBTSxFQUFSLENBQVEsQ0FBQyxDQUFBO3dCQUlsRCxxQkFBTSxTQUFJLENBQUMsT0FBTyxFQUFFLENBQUMsS0FBSyxDQUFDLGdFQUU5QixDQUFDLEVBQUE7O3dCQUhNLGNBQWMsR0FBRyxDQUNyQixTQUVGLENBQ0MsQ0FBQyxTQUFTLENBQUMsa0RBQWtEO3dCQUFuRDt3QkFHUCxXQUFXLEdBQUcsY0FBYyxDQUFDLE1BQU0sQ0FDckMsVUFBQyxDQUFDOzRCQUNBLG1DQUFtQzs0QkFDbkMsT0FBQSxDQUFDLENBQUMsQ0FBQyxlQUFlLEtBQUssSUFBSSxJQUFJLE9BQU8sQ0FBQyxPQUFPLENBQUMsQ0FBQyxDQUFDLFFBQVEsQ0FBQyxJQUFJLENBQUMsQ0FBQztnQ0FDaEUsMERBQTBEO2dDQUMxRCxDQUFDLENBQUMsQ0FBQyxlQUFlLEtBQUssYUFBYSxJQUFJLE9BQU8sQ0FBQyxPQUFPLENBQUMsQ0FBQyxDQUFDLFFBQVEsQ0FBQyxJQUFJLENBQUMsQ0FBQztnQ0FDekUsdUVBQXVFO2dDQUN2RSxlQUFlLENBQUMsSUFBSSxDQUFDLFVBQUMsQ0FBQyxJQUFLLE9BQUEsQ0FBQyxDQUFDLEdBQUcsS0FBSyxDQUFDLENBQUMsZUFBZSxJQUFJLENBQUMsQ0FBQyxNQUFNLEtBQUssQ0FBQyxDQUFDLFFBQVEsRUFBdEQsQ0FBc0QsQ0FBQyxLQUFLLFNBQVM7d0JBSmpHLENBSWlHLENBQ3BHLENBQUE7d0JBSWMscUJBQU0sU0FBSSxDQUFDLE9BQU8sRUFBRSxDQUFDLEtBQUssQ0FBQyw2REFFM0MsQ0FBQyxFQUFBOzt3QkFGTSxNQUFNLEdBQUcsU0FFZjt3QkFDQSxPQUFPLENBQUMsR0FBRyxDQUFDLFdBQVcsR0FBRyxJQUFJLENBQUMsU0FBUyxDQUFDLE1BQU0sQ0FBQyxTQUFTLENBQUMsR0FBRyxVQUFVLENBQUMsQ0FBQTt3QkFFeEUsbURBQW1EO3dCQUNuRCxXQUFXLEdBQUcsV0FBVzs2QkFDdEIsR0FBRyxDQUFDLFVBQUMsQ0FBQzs0QkFDTCxJQUFNLFdBQVcsR0FBRyxDQUFDLENBQUMsVUFBVSxDQUFDLFVBQVUsQ0FBQyxHQUFHLENBQUM7Z0NBQzlDLENBQUMsQ0FBQyxJQUFJLENBQUMsS0FBSyxDQUFDLENBQUMsQ0FBQyxVQUFVLENBQUM7Z0NBQzFCLENBQUMsQ0FBQyxFQUFFLFFBQVEsRUFBRSxFQUFFLEVBQUUsUUFBUSxFQUFFLENBQUMsQ0FBQyxVQUFVLEVBQUUsQ0FBQTs0QkFFNUMsSUFBTSxHQUFHLEdBQUcsSUFBSSx3QkFBaUIsRUFBRSxDQUFBOzRCQUNuQyxHQUFHLENBQUMsR0FBRyxHQUFHLENBQUMsQ0FBQyxhQUFhLENBQUE7NEJBQ3pCLEdBQUcsQ0FBQyxJQUFJLEdBQUcsQ0FBQyxDQUFDLFFBQVEsQ0FBQTs0QkFDckIsR0FBRyxDQUFDLEVBQUUsR0FBRyxDQUFDLENBQUMsZUFBZSxDQUFBOzRCQUMxQixHQUFHLENBQUMsUUFBUSxHQUFHLFdBQVcsQ0FBQyxRQUFRLENBQUE7NEJBQ25DLEdBQUcsQ0FBQyxRQUFRLEdBQUcsV0FBVyxDQUFDLFFBQVEsQ0FBQTs0QkFDbkMsR0FBRyxDQUFDLFFBQVEsR0FBRyxDQUFDLENBQUMsY0FBYyxDQUFBOzRCQUMvQixHQUFHLENBQUMsWUFBWSxHQUFHLElBQUksQ0FBQyxLQUFLLENBQUMsQ0FBQyxDQUFDLFdBQVcsQ0FBQyxDQUFBOzRCQUM1QyxPQUFPLEdBQUcsQ0FBQTt3QkFDWixDQUFDLENBQUM7NkJBQ0QsR0FBRyxDQUFDLFVBQUMsQ0FBQzs0QkFDTCwrREFBK0Q7NEJBQy9ELGtEQUFrRDs0QkFDbEQsSUFBSSxDQUFDLENBQUMsRUFBRSxLQUFLLGFBQWE7Z0NBQ3hCLE9BQU8sZUFBZTtxQ0FDbkIsTUFBTSxDQUFDLFVBQUMsQ0FBQyxJQUFLLE9BQUEsQ0FBQyxDQUFDLElBQUksS0FBSyxhQUFhLElBQUksQ0FBQyxDQUFDLE1BQU0sS0FBSyxDQUFDLENBQUMsSUFBSSxJQUFJLENBQUMsQ0FBQyxHQUFHLEtBQUssQ0FBQyxDQUFDLE1BQU0sRUFBckUsQ0FBcUUsQ0FBQztxQ0FDcEYsR0FBRyxDQUFDLFVBQUMsQ0FBQyxJQUFLLE9BQUEsdUJBQU0sQ0FBQyxLQUFFLEVBQUUsRUFBRSxDQUFDLENBQUMsR0FBRyxJQUFHLEVBQXJCLENBQXFCLENBQUMsQ0FBQTs0QkFDdEMsT0FBTyx1QkFBTSxDQUFDLEtBQUUsRUFBRSxFQUFVLENBQUMsQ0FBQyxJQUFJLElBQUcsQ0FBQTt3QkFDdkMsQ0FBQyxDQUFDLENBQ0g7d0JBQUEsQ0FBQSxLQUFRLEVBQUcsQ0FBQSxDQUFDLE1BQU0sb0JBQUksV0FBVyxHQUFFLE9BQU8sQ0FBQyxVQUFDLENBQUM7NEJBQzVDLE9BQUEsY0FBYyxDQUFDLE9BQU8sQ0FBQyxDQUFDLEVBQUU7Z0NBQ3hCOzttR0FFdUQ7Z0NBQ3ZELEtBQUssRUFDSCxNQUFNO29DQUNOLGFBQU8sQ0FDTCxJQUFJLENBQUMsU0FBUyxDQUFDO3dDQUNiLFFBQVEsRUFBRSxFQUFFLElBQUksRUFBRSxDQUFDLENBQUMsSUFBSSxFQUFFLEVBQUUsRUFBRSxDQUFDLENBQUMsRUFBRSxFQUFFO3dDQUNwQyxRQUFRLEVBQUUsVUFBSTtxQ0FDZixDQUFDLENBQ0g7Z0NBRUgsb0VBQW9FO2dDQUNwRSxNQUFNLEVBQUU7b0NBQ04sRUFBRSxFQUFFLENBQUMsQ0FBQyxFQUFFO29DQUNSLElBQUksRUFBRSxjQUFjLENBQUMsVUFBVSxDQUFDLENBQUMsQ0FBQyxFQUFFLENBQUM7aUNBQ3RDO2dDQUVELHVFQUF1RTtnQ0FDdkUsS0FBSyxFQUFFLENBQUMsZUFBZSxFQUFFLGFBQWEsQ0FBQzs2QkFDeEMsQ0FBQztpQ0FDQyxJQUFJLENBQUMsVUFBQyxDQUFDO2dDQUNOLGNBQWMsQ0FBQyxJQUFJLENBQUMsR0FBRyxFQUFFLENBQUMsQ0FBQyxFQUFHLEVBQVUsQ0FBQyxDQUFDLElBQUssRUFBRSxDQUFDLENBQUMsR0FBSSxHQUFHLFNBQVMsRUFBRSxDQUFDLENBQUMsQ0FBQTs0QkFDekUsQ0FBQyxDQUFDO2lDQUNELEtBQUssQ0FBQyxVQUFDLEdBQUc7Z0NBQ1QsY0FBYyxDQUFDLElBQUksQ0FDakIsR0FBRyxFQUNILENBQUMsQ0FBQyxFQUFHLEVBQ0csQ0FBQyxDQUFDLElBQUssRUFDZixDQUFDLENBQUMsR0FBSSxHQUFHLFNBQVMsRUFDbEIsSUFBSSxDQUFDLFNBQVMsQ0FBQyxFQUFFLE1BQU0sRUFBRSxJQUFJLEVBQUUsSUFBSSxFQUFFLEdBQUcsRUFBRSxDQUFDLENBQzVDLENBQUE7NEJBQ0gsQ0FBQyxDQUFDO3dCQWpDSixDQWlDSSxDQUNMLENBQUE7Ozs7O0tBV0Y7SUFDSCxxQkFBQztBQUFELENBQUMsQUE3WkQsSUE2WkM7QUE3Wlksd0NBQWM7QUErWjNCOztHQUVHO0FBQ0g7O21CQUVtQjtBQUVuQiw2RUFBNkU7QUFDN0U7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7O0VBcURFO0FBRUYsWUFBWTtBQUNaLG1EQUFtRDtBQUNuRCxxR0FBcUc7QUFDckcsc0dBQXNHO0FBQ3RHLGtCQUFrQjtBQUNsQiw2QkFBNkI7QUFDN0IsaUZBQWlGO0FBQ2pGLDZEQUE2RDtBQUM3RCxrREFBa0Q7QUFDbEQsRUFBRTtBQUNGLFlBQVk7QUFDWixtQ0FBbUM7QUFDbkMsd0NBQXdDO0FBQ3hDLGtFQUFrRTtBQUNsRSw4QkFBOEI7QUFDOUIseUJBQXlCO0FBQ3pCLDJEQUEyRDtBQUMzRCxnQ0FBZ0M7QUFDaEMsb0NBQW9DO0FBRXBDLHFEQUFxRDtBQUNyRDs7Ozs7Ozs7O0VBU0U7QUFFRjs7Ozs7Ozs7Ozs7Ozs7Ozs7OztFQW1CRSJ9