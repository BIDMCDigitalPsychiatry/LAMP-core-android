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
var crypto_1 = __importDefault(require("crypto"));
var SQL;
/*
ALTER DATABASE LAMP SET CHANGE_TRACKING = ON (CHANGE_RETENTION = 14 DAYS, AUTO_CLEANUP = ON);
sp_MSforeachtable @command1 = "ALTER TABLE ? ENABLE CHANGE_TRACKING WITH (TRACK_COLUMNS_UPDATED = ON)";
*/
/*
ALTER DATABASE LAMP SET CHANGE_TRACKING = OFF;
sp_MSforeachtable @command1 = "ALTER TABLE ? DISABLE CHANGE_TRACKING";
*/
/*
sp_MSforeachtable @command1 = "SELECT * FROM CHANGETABLE(CHANGES ?, 0) AS C";
*/
/*
WITH CHANGE_TRACKING_CONTEXT(0xC0DED00D)
UPDATE LAMP.dbo.Admin
SET EditedOn = NULL
WHERE AdminID = 99;
*/
exports.Setup = function (key) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        return [2 /*return*/];
    });
}); };
exports.Changes = function () { return __awaiter(void 0, void 0, void 0, function () {
    var _a, _b, _c;
    var _d;
    return __generator(this, function (_e) {
        switch (_e.label) {
            case 0:
                _a = "";
                _c = (_b = Number).parse;
                return [4 /*yield*/, SQL.request().query("SELECT CHANGE_TRACKING_CURRENT_VERSION();")];
            case 1: return [2 /*return*/, (_a + (_d = _c.apply(_b, [(_e.sent()).recordset[0][""]]), (_d !== null && _d !== void 0 ? _d : 0)))];
        }
    });
}); };
exports.Encrypt = function (data, mode) {
    if (mode === void 0) { mode = "Rijndael"; }
    try {
        if (mode === "Rijndael") {
            var cipher = crypto_1.default.createCipheriv("aes-256-ecb", process.env.DB_KEY || "", "");
            return cipher.update(data, "utf8", "base64") + cipher.final("base64");
        }
        else if (mode === "AES256") {
            var ivl = crypto_1.default.randomBytes(16);
            var cipher = crypto_1.default.createCipheriv("aes-256-cbc", Buffer.from(process.env.ROOT_KEY || "", "hex"), ivl);
            return Buffer.concat([ivl, cipher.update(Buffer.from(data, "utf16le")), cipher.final()]).toString("base64");
        }
    }
    catch (_a) { }
    return undefined;
};
exports.Decrypt = function (data, mode) {
    if (mode === void 0) { mode = "Rijndael"; }
    try {
        if (mode === "Rijndael") {
            var cipher = crypto_1.default.createDecipheriv("aes-256-ecb", process.env.DB_KEY || "", "");
            return cipher.update(data, "base64", "utf8") + cipher.final("utf8");
        }
        else if (mode === "AES256") {
            var dat = Buffer.from(data, "base64");
            var cipher = crypto_1.default.createDecipheriv("aes-256-cbc", Buffer.from(process.env.ROOT_KEY || "", "hex"), dat.slice(0, 16));
            return Buffer.concat([cipher.update(dat.slice(16)), cipher.final()]).toString("utf16le");
        }
    }
    catch (_a) { }
    return undefined;
};
var Identifier = /** @class */ (function () {
    function Identifier() {
    }
    Identifier.pack = function (components) {
        if (components.length === 0)
            return "";
        return Buffer.from(components.join(":")).toString("base64").replace(/=/g, "~");
    };
    Identifier.unpack = function (components) {
        if (components.match(/^G?U\d+$/))
            return [];
        return Buffer.from(components.replace(/~/g, "="), "base64")
            .toString("utf8")
            .split(":");
    };
    Identifier.researcher_pack = function (components) {
        return Identifier.pack([Researcher.name, components.admin_id || 0]);
    };
    Identifier.researcher_unpack = function (id) {
        var components = Identifier.unpack(id);
        if (components[0] !== Researcher.name)
            throw new Error("invalid identifier");
        var result = components.slice(1).map(function (x) { return parseInt(x); });
        return {
            admin_id: !isNaN(result[0]) ? result[0] : 0,
        };
    };
    Identifier.study_pack = function (components) {
        return Identifier.pack([Study.name, components.admin_id || 0]);
    };
    Identifier.study_unpack = function (id) {
        var components = Identifier.unpack(id);
        if (components[0] !== Study.name)
            throw new Error("invalid identifier");
        var result = components.slice(1).map(function (x) { return parseInt(x); });
        return {
            admin_id: !isNaN(result[0]) ? result[0] : 0,
        };
    };
    Identifier.participant_pack = function (components) {
        return components.study_id || "";
    };
    Identifier.participant_unpack = function (id) {
        return { study_id: id };
    };
    Identifier.activity_pack = function (components) {
        return Identifier.pack([
            Activity.name,
            components.activity_spec_id || 0,
            components.admin_id || 0,
            components.survey_id || 0,
        ]);
    };
    Identifier.activity_unpack = function (id) {
        var components = Identifier.unpack(id);
        if (components[0] !== Activity.name)
            throw new Error("invalid identifier");
        var result = components.slice(1).map(function (x) { return parseInt(x); });
        return {
            activity_spec_id: !isNaN(result[0]) ? result[0] : 0,
            admin_id: !isNaN(result[1]) ? result[1] : 0,
            survey_id: !isNaN(result[2]) ? result[2] : 0,
        };
    };
    return Identifier;
}());
var Researcher = /** @class */ (function () {
    function Researcher() {
    }
    Researcher._select = function (change_version) {
        return __awaiter(this, void 0, void 0, function () {
            var admin_id;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, SQL.request().query("\n\t\t\tSELECT \n                AdminID AS id, \n                isDeleted AS deleted,\n                FirstName AS name, \n                LastName AS lname\n            FROM Admin\n            WHERE 1=1\n            \t" + (!!admin_id ? "AND AdminID = '" + admin_id + "'" : "") + "\n            \t" + (!!change_version
                            ? "AND AdminID IN (\n            \t\tSELECT AdminID \n            \t\tFROM CHANGETABLE(CHANGES Admin, " + change_version + ") AS C \n            \t\tWHERE SYS_CHANGE_CONTEXT IS NULL\n            \t)"
                            : "") + "\n\t\t;")];
                    case 1: 
                    /*
                        let id = undefined
                        if (!!id && Identifier.unpack(id)[0] === (<any>Researcher).name)
                            admin_id = Identifier.researcher_unpack(id).admin_id
                        else if(!!id) throw new Error()
                        */
                    return [2 /*return*/, (_a.sent()).recordset.map(function (raw) {
                            var obj = {};
                            obj._id = Identifier.researcher_pack({ admin_id: raw.id });
                            obj._deleted = raw.deleted;
                            obj.$_sync_id = raw.id;
                            obj.$_parent_id = null;
                            obj.name = [exports.Decrypt(raw.name), exports.Decrypt(raw.lname)].join(" ");
                            return obj;
                        })];
                }
            });
        });
    };
    Researcher._upsert = function (documents) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, SQL.request().query("\n\t\t\tWITH CHANGE_TRACKING_CONTEXT(0xC0DED00D)\n\t\t\tMERGE INTO LAMP.dbo.Admin\n\t\t\t\tWITH (HOLDLOCK) AS Target\n\t\t\tUSING (VALUES " + documents
                            .map(function (x) { return "(\n\t\t\t\t" + (!!x._id ? "'" + x._id + "'" : "NULL") + ",\n\t\t\t\t" + (!!x.$_sync_id ? x.$_sync_id : "NULL") + ",\n\t\t\t\t" + (typeof x._deleted === "boolean" ? (x._deleted === true ? "1" : "0") : "NULL") + ",\n\t\t\t\t" + (!!x.name ? "'" + exports.Encrypt(x.name.split(" ")[0] || "") + "'" : "NULL") + ",\n\t\t\t\t" + (!!x.name ? "'" + exports.Encrypt(x.name.split(" ")[1] || "") + "'" : "NULL") + "\n\t\t\t)"; })
                            .join(",") + ") AS Source(\n\t\t\t\tOriginalID,\n\t\t\t\tAdminID,\n\t\t\t\tIsDeleted,\n\t\t\t\tFirstName,\n\t\t\t\tLastName\n\t\t\t)\n\t\t\tON Target.AdminID = Source.AdminID\n\t\t\tWHEN MATCHED THEN \n\t\t\t\tUPDATE SET \n\t\t\t\t\tTarget.IsDeleted = CASE \n\t\t\t\t\t\tWHEN Source.IsDeleted IS NULL \n\t\t\t\t\t\tTHEN Target.IsDeleted \n\t\t\t\t\t\tELSE Source.IsDeleted\n\t\t\t\t\tEND, \n\t                Target.FirstName = CASE \n\t                \tWHEN Source.FirstName IS NULL \n\t                \tTHEN Target.FirstName \n\t                \tELSE Source.FirstName \n                \tEND, \n\t                Target.LastName = CASE \n\t                \tWHEN Source.LastName IS NULL \n\t                \tTHEN Target.LastName \n\t                \tELSE Source.LastName \n                \tEND\n\t\t\tWHEN NOT MATCHED THEN \n\t\t\t\tINSERT (\n\t\t\t\t\tEmail, \n\t                FirstName, \n\t                LastName, \n\t                CreatedOn, \n\t                AdminType\n                ) VALUES (\n\t\t\t\t\t'', \n\t                CASE \n\t                \tWHEN Source.FirstName IS NULL \n\t                \tTHEN '' \n\t                \tELSE Source.FirstName \n                \tEND, \n\t                CASE \n\t                \tWHEN Source.LastName IS NULL \n\t                \tTHEN '' \n\t                \tELSE Source.LastName \n                \tEND, \n\t\t\t\t\tGETDATE(), \n\t\t\t\t\t2\n                )\n\t\t\tOUTPUT \n\t\t\t\t$action AS Action, \n\t\t\t\tSource.OriginalID AS SourceID,\n\t\t\t\tINSERTED.AdminID AS TargetID\n\t\t;")];
                    case 1: return [2 /*return*/, (_a.sent()).recordset];
                }
            });
        });
    };
    return Researcher;
}());
exports.Researcher = Researcher;
var Study = /** @class */ (function () {
    function Study() {
    }
    Study._select = function (change_version) {
        return __awaiter(this, void 0, void 0, function () {
            var admin_id;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, SQL.request().query("\n\t\t\tSELECT \n                AdminID AS id, \n                isDeleted AS deleted,\n                FirstName AS name, \n                LastName AS lname\n            FROM Admin\n            WHERE 1=1\n            \t" + (!!admin_id ? "AND AdminID = '" + admin_id + "'" : "") + "\n            \t" + (!!change_version
                            ? "AND AdminID IN (\n        \t\t\tSELECT AdminID \n        \t\t\tFROM CHANGETABLE(CHANGES Admin, " + change_version + ") AS C \n        \t\t\tWHERE SYS_CHANGE_CONTEXT IS NULL\n    \t\t\t)"
                            : "") + "\n\t\t;")];
                    case 1: 
                    /*
                        let id = undefined
                        if (!!id && Identifier.unpack(id)[0] === (<any>Researcher).name)
                            admin_id = Identifier.researcher_unpack(id).admin_id
                        else if(!!id) throw new Error()
                        */
                    return [2 /*return*/, (_a.sent()).recordset.map(function (raw) {
                            var obj = {};
                            obj._id = Identifier.study_pack({ admin_id: raw.id });
                            obj._deleted = raw.deleted;
                            obj.$_sync_id = raw.id;
                            obj.$_parent_id = Identifier.researcher_pack({ admin_id: raw.id });
                            obj.name = [exports.Decrypt(raw.name), exports.Decrypt(raw.lname)].join(" ");
                            return obj;
                        })];
                }
            });
        });
    };
    Study._upsert = function (documents) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, SQL.request().query("\n\t\t\tWITH CHANGE_TRACKING_CONTEXT(0xC0DED00D)\n\t\t\tMERGE INTO LAMP.dbo.Admin\n\t\t\t\tWITH (HOLDLOCK) AS Target\n\t\t\tUSING (VALUES " + documents
                            .map(function (x) { return "(\n\t\t\t\t" + (!!x._id ? "'" + x._id + "'" : "NULL") + ",\n\t\t\t\t" + (!!x.$_sync_id ? x.$_sync_id : "NULL") + ",\n\t\t\t\t" + (typeof x._deleted === "boolean" ? (x._deleted === true ? "1" : "0") : "NULL") + ",\n\t\t\t\t" + (!!x.name ? "'" + exports.Encrypt(x.name.split(" ")[0] || "") + "'" : "NULL") + ",\n\t\t\t\t" + (!!x.name ? "'" + exports.Encrypt(x.name.split(" ")[1] || "") + "'" : "NULL") + "\n\t\t\t)"; })
                            .join(",") + ") AS Source(\n\t\t\t\tOriginalID,\n\t\t\t\tAdminID,\n\t\t\t\tIsDeleted,\n\t\t\t\tFirstName,\n\t\t\t\tLastName\n\t\t\t)\n\t\t\tON Target.AdminID = Source.AdminID\n\t\t\tWHEN MATCHED THEN \n\t\t\t\tUPDATE SET \n\t\t\t\t\tTarget.IsDeleted = CASE \n\t\t\t\t\t\tWHEN Source.IsDeleted IS NULL \n\t\t\t\t\t\tTHEN Target.IsDeleted \n\t\t\t\t\t\tELSE Source.IsDeleted\n\t\t\t\t\tEND, \n\t                Target.FirstName = CASE \n\t                \tWHEN Source.FirstName IS NULL \n\t                \tTHEN Target.FirstName \n\t                \tELSE Source.FirstName \n                \tEND, \n\t                Target.LastName = CASE \n\t                \tWHEN Source.LastName IS NULL \n\t                \tTHEN Target.LastName \n\t                \tELSE Source.LastName \n                \tEND\n\t\t\tWHEN NOT MATCHED THEN \n\t\t\t\tINSERT (\n\t\t\t\t\tEmail, \n\t                FirstName, \n\t                LastName, \n\t                CreatedOn, \n\t                AdminType\n                ) VALUES (\n\t\t\t\t\t'', \n\t                CASE \n\t                \tWHEN Source.FirstName IS NULL \n\t                \tTHEN '' \n\t                \tELSE Source.FirstName \n                \tEND, \n\t                CASE \n\t                \tWHEN Source.LastName IS NULL \n\t                \tTHEN '' \n\t                \tELSE Source.LastName \n                \tEND, \n\t\t\t\t\tGETDATE(), \n\t\t\t\t\t2\n                )\n\t\t\tOUTPUT \n\t\t\t\t$action AS Action, \n\t\t\t\tSource.OriginalID AS SourceID,\n\t\t\t\tINSERTED.AdminID AS TargetID\n\t\t;")];
                    case 1: return [2 /*return*/, (_a.sent()).recordset];
                }
            });
        });
    };
    return Study;
}());
exports.Study = Study;
var Participant = /** @class */ (function () {
    function Participant() {
    }
    Participant._select = function (change_version) {
        return __awaiter(this, void 0, void 0, function () {
            var user_id, admin_id;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, SQL.request().query("\n            SELECT \n            \tUsers.UserID AS [_id],\n            \tUsers.AdminID AS [_parent],\n                StudyId AS id, \n                StudyCode AS study_code, \n                AppColor AS [theme], \n                Language AS [language], \n                [24By7ContactNo] AS [emergency_contact],\n                PersonalHelpline AS [helpline],\n                Users.IsDeleted AS deleted\n            FROM Users\n            FULL OUTER JOIN UserSettings\n                ON UserSettings.UserID = Users.UserID\n            FULL OUTER JOIN UserDevices\n                ON UserDevices.UserID = Users.UserID\n            WHERE 1=1\n            \t" + (!!user_id ? "AND Users.StudyId = '" + exports.Encrypt(user_id) + "'" : "") + " \n            \t" + (!!admin_id ? "AND Users.AdminID = '" + admin_id + "'" : "") + "\n            \t" + (!!change_version
                            ? "AND Users.UserID IN (\n        \t\t\tSELECT UserID \n        \t\t\tFROM CHANGETABLE(CHANGES Users, " + change_version + ") AS C \n        \t\t\tWHERE SYS_CHANGE_CONTEXT IS NULL\n    \t\t\t)"
                            : "") + "\n\t    ;")];
                    case 1: 
                    /*
                        let id = undefined
                        if (!!id && Identifier.unpack(id)[0] === (<any>Researcher).name)
                            admin_id = Identifier.researcher_unpack(id).admin_id
                        else if (!!id && Identifier.unpack(id).length === 0) // 0 = Participant
                            user_id = Identifier.participant_unpack(id).study_id
                        else if(!!id) throw new Error()
                        */
                    return [2 /*return*/, (_a.sent()).recordset.map(function (raw) {
                            var obj = {};
                            obj._id = exports.Decrypt(raw.id);
                            obj._deleted = raw.deleted;
                            obj.$_sync_id = raw._id;
                            obj.$_parent_id = Identifier.study_pack({ admin_id: raw._parent });
                            return obj;
                        })];
                }
            });
        });
    };
    Participant._upsert = function (documents) {
        return __awaiter(this, void 0, void 0, function () {
            var result, created, err_1;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, SQL.request().query("\n\t\t\tWITH CHANGE_TRACKING_CONTEXT(0xC0DED00D)\n\t\t\tMERGE INTO LAMP.dbo.Users\n\t\t\t\tWITH (HOLDLOCK) AS Target\n\t\t\tUSING (VALUES " + documents
                            .map(function (x) { return "(\n\t\t\t\t'" + x._id + "',\n\t\t\t\t'" + exports.Encrypt(x._id) + "',\n\t\t\t\t'" + exports.Encrypt(x._id + "@lamp.com") + "',\n\t\t\t\t" + (x.$_sync_id || "NULL") + ",\n\t\t\t\t" + (x.$_parent_id || "NULL") + ",\n\t\t\t\t" + (!!x._deleted ? "1" : "0") + "\n\t\t\t)"; })
                            .join(",") + ") AS Source(\n\t\t\t\tOriginalID,\n\t\t\t\tStudyId,\n\t\t\t\tEmail,\n\t\t\t\tUserID,\n\t\t\t\tAdminID,\n\t\t\t\tIsDeleted\n\t\t\t)\n\t\t\tON Target.UserID = Source.UserID\n\t\t\tWHEN MATCHED THEN \n\t\t\t\tUPDATE SET \n\t\t\t\t\tTarget.StudyId = Source.StudyId, \n\t\t\t\t\tTarget.Email = Source.Email,\n\t\t\t\t\tTarget.AdminID = Source.AdminID, \n\t\t\t\t\tTarget.IsDeleted = Source.IsDeleted\n\t\t\tWHEN NOT MATCHED THEN \n\t\t\t\tINSERT (\n\t                Email, \n\t                Password,\n\t                StudyCode, \n\t                StudyId, \n\t                CreatedOn, \n\t                Status,\n\t                AdminID\n                ) VALUES (\n\t\t\t\t\tEmail, \n\t                '', \n\t                '" + exports.Encrypt("001") + "', \n\t\t\t\t\tStudyId, \n\t\t\t        GETDATE(), \n\t\t\t        0,\n\t\t\t        AdminID\n                )\n\t\t\tOUTPUT \n\t\t\t\t$action AS Action, \n\t\t\t\tSource.OriginalID AS SourceID,\n\t\t\t\tINSERTED.UserID AS TargetID\n\t\t;")];
                    case 1:
                        result = _a.sent();
                        created = result.recordset.filter(function (x) { return x.Action === "INSERT"; });
                        if (created.length === 0)
                            return [2 /*return*/, result.recordset];
                        _a.label = 2;
                    case 2:
                        _a.trys.push([2, 4, , 5]);
                        return [4 /*yield*/, SQL.request().query("\n\t\t\t\tWITH CHANGE_TRACKING_CONTEXT(0xC0DED00D)\n\t            INSERT INTO UserSettings (\n\t                UserID, \n\t                AppColor,\n\t                SympSurvey_SlotID,\n\t                SympSurvey_RepeatID,\n\t                CognTest_SlotID,\n\t                CognTest_RepeatID,\n\t                [24By7ContactNo], \n\t                PersonalHelpline,\n\t                PrefferedSurveys,\n\t                PrefferedCognitions,\n\t                Language\n\t            )\n\t\t\t\tVALUES " + created
                                .map(function (x) { return "(\n\t\t\t\t    " + x.TargetID + ",\n\t\t\t        '" + exports.Encrypt("#359FFE") + "',\n\t\t\t        1,\n\t\t\t        1,\n\t\t\t        1,\n\t\t\t        1,\n\t\t\t        '',\n\t\t\t        '',\n\t\t\t        '',\n\t\t\t        '',\n\t\t\t        'en'\n\t\t\t\t)"; })
                                .join(",") + "\n\t\t\t;")];
                    case 3:
                        _a.sent();
                        return [3 /*break*/, 5];
                    case 4:
                        err_1 = _a.sent();
                        console.log(err_1);
                        return [3 /*break*/, 5];
                    case 5: return [2 /*return*/, result.recordset];
                }
            });
        });
    };
    return Participant;
}());
exports.Participant = Participant;
var Activity = /** @class */ (function () {
    function Activity() {
    }
    Activity._select = function (change_version) {
        return __awaiter(this, void 0, void 0, function () {
            var ctest_id, survey_id, admin_id, spec_map, ctest, result;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        spec_map = {
                            "lamp.survey": "Survey",
                            "lamp.nback": "N-Back",
                            "lamp.trails_b": "Trails B",
                            "lamp.spatial_span": "Spatial Span",
                            "lamp.simple_memory": "Simple Memory",
                            "lamp.serial7s": "Serial 7s",
                            "lamp.cats_and_dogs": "Cats and Dogs",
                            "lamp.3d_figure_copy": "3D Figure Copy",
                            "lamp.visual_association": "Visual Association",
                            "lamp.digit_span": "Digit Span",
                            "lamp.cats_and_dogs_new": "Cats and Dogs New",
                            "lamp.temporal_order": "Temporal Order",
                            "lamp.nback_new": "N-Back New",
                            "lamp.trails_b_new": "Trails B New",
                            "lamp.trails_b_dot_touch": "Trails B Dot Touch",
                            "lamp.jewels_a": "Jewels Trails A",
                            "lamp.jewels_b": "Jewels Trails B",
                            "lamp.scratch_image": "Scratch Image",
                            "lamp.spin_wheel": "Spin Wheel",
                        };
                        return [4 /*yield*/, SQL.request().query("\n\t\t\tSELECT \n\t\t\t\tAdminID AS aid,\n\t\t\t\tAdmin.IsDeleted AS deleted,\n\t\t\t\t('ctest') AS type,\n\t\t\t\tCTest.*,\n\t\t\t\t(\n\t\t\t\t\tSELECT \n\t\t\t\t\t\tNoOfSeconds_Beg AS beginner_seconds,\n\t\t\t\t\t\tNoOfSeconds_Int AS intermediate_seconds,\n\t\t\t\t\t\tNoOfSeconds_Adv AS advanced_seconds,\n\t\t\t\t\t\tNoOfSeconds_Exp AS expert_seconds,\n\t\t\t\t\t\tNoOfDiamonds AS diamond_count,\n\t\t\t\t\t\tNoOfShapes AS shape_count,\n\t\t\t\t\t\tNoOfBonusPoints AS bonus_point_count,\n\t\t\t\t\t\tX_NoOfChangesInLevel AS x_changes_in_level_count,\n\t\t\t\t\t\tX_NoOfDiamonds AS x_diamond_count,\n\t\t\t\t\t\tY_NoOfChangesInLevel AS y_changes_in_level_count,\n\t\t\t\t\t\tY_NoOfShapes AS y_shape_count\n\t\t\t\t\tFROM Admin_JewelsTrailsASettings\n\t\t\t\t\tWHERE Admin_JewelsTrailsASettings.AdminID = Admin.AdminID\n\t\t\t\t\t\tAND CTest.lid = 17\n\t\t\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES\n\t\t\t\t) AS [settings.jewelsA],\n\t\t\t\t(\n\t\t\t\t\tSELECT\n\t\t\t\t\t\tNoOfSeconds_Beg AS beginner_seconds,\n\t\t\t\t\t\tNoOfSeconds_Int AS intermediate_seconds,\n\t\t\t\t\t\tNoOfSeconds_Adv AS advanced_seconds,\n\t\t\t\t\t\tNoOfSeconds_Exp AS expert_seconds,\n\t\t\t\t\t\tNoOfDiamonds AS diamond_count,\n\t\t\t\t\t\tNoOfShapes AS shape_count,\n\t\t\t\t\t\tNoOfBonusPoints AS bonus_point_count,\n\t\t\t\t\t\tX_NoOfChangesInLevel AS x_changes_in_level_count,\n\t\t\t\t\t\tX_NoOfDiamonds AS x_diamond_count,\n\t\t\t\t\t\tY_NoOfChangesInLevel AS y_changes_in_level_count,\n\t\t\t\t\t\tY_NoOfShapes AS y_shape_count\n\t\t\t\t\tFROM Admin_JewelsTrailsBSettings\n\t\t\t\t\tWHERE Admin_JewelsTrailsBSettings.AdminID = Admin.AdminID\n\t\t\t\t\t\tAND CTest.lid = 18\n\t\t\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES\n\t\t\t\t) AS [settings.jewelsB],\n\t\t\t\t(\n\t\t\t\t\tSELECT\n\t\t\t\t\t\tVersion as version,\n\t\t\t\t\t\tScheduleDate as schedule_date,\n\t\t\t\t\t\tTime as time,\n\t\t\t\t\t\tRepeatID as repeat_interval,\n\t\t\t\t\t\tJSON_QUERY(dbo.UNWRAP_JSON((\n\t\t\t\t\t\t\tSELECT \n\t\t\t\t\t\t\t\tTime AS t\n\t\t\t\t\t\t\tFROM Admin_CTestScheduleCustomTime\n\t\t\t\t\t\t\tWHERE Admin_CTestScheduleCustomTime.AdminCTestSchId = Admin_CTestSchedule.AdminCTestSchId\n\t\t\t\t\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES\n\t\t\t\t\t\t), 't')) AS custom_time\n\t\t\t\t\tFROM Admin_CTestSchedule\n\t\t\t\t\tWHERE Admin_CTestSchedule.AdminID = Admin.AdminID\n\t\t\t\t\t\tAND Admin_CTestSchedule.CTestID = CTest.lid\n\t\t\t\t\t\tAND Admin_CTestSchedule.IsDeleted = 0\n\t\t\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES\n\t\t\t\t) AS schedule\n\t\t\tFROM Admin\n\t\t\tCROSS APPLY \n\t\t\t(\n\t\t\t\tSELECT \n\t\t\t\t\tActivityIndexID AS id,\n\t\t\t\t\tLegacyCTestID AS lid,\n\t\t\t\t\tName AS name\n\t\t\t\tFROM LAMP_Aux.dbo.ActivityIndex\n\t\t\t\tWHERE LegacyCTestID IS NOT NULL\n\t\t\t) AS CTest\n\t\t\tWHERE 1=1\n\t\t\t\t" + (!ctest_id ? "" : "AND CTest.id = '" + ctest_id + "'") + "\n\t\t\t\t" + (!admin_id ? "" : "AND AdminID = '" + admin_id + "'") + "\n\t\t\t\t" + (!!change_version ? "AND 1=0" : "") + "\n\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES, WITHOUT_ARRAY_WRAPPER\n\t\t;")];
                    case 1:
                        ctest = _a.sent();
                        return [4 /*yield*/, SQL.request().query("\n\t\tWITH A(value) AS (\n\t\t\tSELECT \n\t\t\t\tAdminID AS aid,\n\t\t\t\tAdmin.IsDeleted AS deleted,\n\t\t\t\t('ctest') AS type,\n\t\t\t\tCTest.*,\n\t\t\t\t(\n\t\t\t\t\tSELECT \n\t\t\t\t\t\tNoOfSeconds_Beg AS beginner_seconds,\n\t\t\t\t\t\tNoOfSeconds_Int AS intermediate_seconds,\n\t\t\t\t\t\tNoOfSeconds_Adv AS advanced_seconds,\n\t\t\t\t\t\tNoOfSeconds_Exp AS expert_seconds,\n\t\t\t\t\t\tNoOfDiamonds AS diamond_count,\n\t\t\t\t\t\tNoOfShapes AS shape_count,\n\t\t\t\t\t\tNoOfBonusPoints AS bonus_point_count,\n\t\t\t\t\t\tX_NoOfChangesInLevel AS x_changes_in_level_count,\n\t\t\t\t\t\tX_NoOfDiamonds AS x_diamond_count,\n\t\t\t\t\t\tY_NoOfChangesInLevel AS y_changes_in_level_count,\n\t\t\t\t\t\tY_NoOfShapes AS y_shape_count\n\t\t\t\t\tFROM Admin_JewelsTrailsASettings\n\t\t\t\t\tWHERE Admin_JewelsTrailsASettings.AdminID = Admin.AdminID\n\t\t\t\t\t\tAND CTest.lid = 17\n\t\t\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES\n\t\t\t\t) AS [settings.jewelsA],\n\t\t\t\t(\n\t\t\t\t\tSELECT\n\t\t\t\t\t\tNoOfSeconds_Beg AS beginner_seconds,\n\t\t\t\t\t\tNoOfSeconds_Int AS intermediate_seconds,\n\t\t\t\t\t\tNoOfSeconds_Adv AS advanced_seconds,\n\t\t\t\t\t\tNoOfSeconds_Exp AS expert_seconds,\n\t\t\t\t\t\tNoOfDiamonds AS diamond_count,\n\t\t\t\t\t\tNoOfShapes AS shape_count,\n\t\t\t\t\t\tNoOfBonusPoints AS bonus_point_count,\n\t\t\t\t\t\tX_NoOfChangesInLevel AS x_changes_in_level_count,\n\t\t\t\t\t\tX_NoOfDiamonds AS x_diamond_count,\n\t\t\t\t\t\tY_NoOfChangesInLevel AS y_changes_in_level_count,\n\t\t\t\t\t\tY_NoOfShapes AS y_shape_count\n\t\t\t\t\tFROM Admin_JewelsTrailsBSettings\n\t\t\t\t\tWHERE Admin_JewelsTrailsBSettings.AdminID = Admin.AdminID\n\t\t\t\t\t\tAND CTest.lid = 18\n\t\t\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES\n\t\t\t\t) AS [settings.jewelsB],\n\t\t\t\t(\n\t\t\t\t\tSELECT\n\t\t\t\t\t\tVersion as version,\n\t\t\t\t\t\tScheduleDate as schedule_date,\n\t\t\t\t\t\tTime as time,\n\t\t\t\t\t\tRepeatID as repeat_interval,\n\t\t\t\t\t\tJSON_QUERY(dbo.UNWRAP_JSON((\n\t\t\t\t\t\t\tSELECT \n\t\t\t\t\t\t\t\tTime AS t\n\t\t\t\t\t\t\tFROM Admin_CTestScheduleCustomTime\n\t\t\t\t\t\t\tWHERE Admin_CTestScheduleCustomTime.AdminCTestSchId = Admin_CTestSchedule.AdminCTestSchId\n\t\t\t\t\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES\n\t\t\t\t\t\t), 't')) AS custom_time\n\t\t\t\t\tFROM Admin_CTestSchedule\n\t\t\t\t\tWHERE Admin_CTestSchedule.AdminID = Admin.AdminID\n\t\t\t\t\t\tAND Admin_CTestSchedule.CTestID = CTest.lid\n\t\t\t\t\t\tAND Admin_CTestSchedule.IsDeleted = 0\n\t\t\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES\n\t\t\t\t) AS schedule\n\t\t\tFROM Admin\n\t\t\tCROSS APPLY \n\t\t\t(\n\t\t\t\tSELECT \n\t\t\t\t\tActivityIndexID AS id,\n\t\t\t\t\tLegacyCTestID AS lid,\n\t\t\t\t\tName AS name\n\t\t\t\tFROM LAMP_Aux.dbo.ActivityIndex\n\t\t\t\tWHERE LegacyCTestID IS NOT NULL\n\t\t\t) AS CTest\n\t\t\tWHERE 1=1\n\t\t\t\t" + (!ctest_id ? "" : "AND CTest.id = '" + ctest_id + "'") + "\n\t\t\t\t" + (!admin_id ? "" : "AND AdminID = '" + admin_id + "'") + "\n\t\t\t\t" + (!!change_version ? "AND 1=0" : "") + "\n\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES, WITHOUT_ARRAY_WRAPPER\n\t\t), B(value) AS (\n\t\t\tSELECT \n\t\t\t\tSurveyID AS id, \n\t\t\t\tAdminID AS aid,\n\t\t\t\tSurvey.isDeleted AS deleted,\n\t\t\t\tSurveyName AS name, \n\t\t\t\t('survey') AS type,\n\t\t\t\t(\n\t\t\t\t\tSELECT \n\t\t\t\t\t\tQuestionText AS text, \n\t\t\t\t\t\tCHOOSE(AnswerType, \n\t\t\t\t\t\t\t'likert', 'list', 'boolean', 'clock', 'years', 'months', 'days'\n\t\t\t\t\t\t) AS type, \n\t\t\t\t\t\tJSON_QUERY(dbo.UNWRAP_JSON((\n\t\t\t\t\t\t\tSELECT \n\t\t\t\t\t\t\t\tOptionText AS opt\n\t\t\t\t\t\t\tFROM SurveyQuestionOptions\n\t\t\t\t\t\t\tWHERE SurveyQuestionOptions.QuestionID = SurveyQuestions.QuestionID\n\t\t\t\t\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES\n\t\t\t\t\t\t), 'opt')) AS options\n\t\t\t\t\t\tFROM SurveyQuestions\n\t\t\t\t\t\tWHERE IsDeleted = 0 \n\t\t\t\t\t\t\tAND SurveyQuestions.SurveyID = Survey.SurveyID\n\t\t\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES\n\t\t\t\t) AS questions,\n\t\t\t\t(\n\t\t\t\t\tSELECT\n\t\t\t\t\t\tScheduleDate as schedule_date,\n\t\t\t\t\t\tTime as time,\n\t\t\t\t\t\tRepeatID as repeat_interval,\n\t\t\t\t\t\tJSON_QUERY(dbo.UNWRAP_JSON((\n\t\t\t\t\t\t\tSELECT \n\t\t\t\t\t\t\t\tTime AS t\n\t\t\t\t\t\t\tFROM Admin_SurveyScheduleCustomTime\n\t\t\t\t\t\t\tWHERE Admin_SurveyScheduleCustomTime.AdminSurveySchId = Admin_SurveySchedule.AdminSurveySchId\n\t\t\t\t\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES\n\t\t\t\t\t\t), 't')) AS custom_time\n\t\t\t\t\tFROM Admin_SurveySchedule\n\t\t\t\t\tWHERE Admin_SurveySchedule.SurveyID = Survey.SurveyID\n\t\t\t\t\t\tAND Admin_SurveySchedule.IsDeleted = 0\n\t\t\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES\n\t\t\t\t) AS schedule\n\t\t\tFROM Survey\n\t\t\tWHERE 1=1\n\t\t\t\t" + (!ctest_id ? "" : ctest_id === 1 /* survey */ ? "" : "AND 1=0") + "\n\t\t\t\t" + (!survey_id ? "" : "AND SurveyID = '" + survey_id + "'") + "\n\t\t\t\t" + (!admin_id ? "" : "AND AdminID = '" + admin_id + "'") + "\n\t\t\t\t" + (!!change_version ? "AND 1=0" : "") + "\n\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES, WITHOUT_ARRAY_WRAPPER\n\t\t)\n\t\tSELECT CONCAT('[', A.value, CASE \n\t\t\tWHEN LEN(A.value) > 0 AND LEN(B.value) > 0 THEN ',' ELSE '' \n\t\tEND, B.value, ']')\n\t\tFROM A, B\n\t\t;")];
                    case 2:
                        result = _a.sent();
                        return [2 /*return*/, JSON.parse(result.recordset[0][""]).map(function (raw) {
                                var obj = {};
                                if (raw.type === "ctest") {
                                    obj._id = Identifier.activity_pack({
                                        activity_spec_id: raw.id,
                                        admin_id: raw.aid,
                                    });
                                    obj._deleted = raw.deleted;
                                    obj.$_sync_id = [raw.id, raw.aid, null];
                                    obj.$_parent_id = Identifier.study_pack({ admin_id: raw.aid });
                                    obj.spec = raw.name;
                                    obj.name = spec_map[raw.name];
                                    obj.settings = __assign(__assign({}, (raw.settings.jewelsA || { "0": {} })["0"]), (raw.settings.jewelsB || { "0": {} })["0"]);
                                }
                                else if (raw.type === "survey") {
                                    obj._id = Identifier.activity_pack({
                                        activity_spec_id: 1 /* survey */,
                                        admin_id: raw.aid,
                                        survey_id: raw.id,
                                    });
                                    obj._deleted = raw.deleted;
                                    obj.$_sync_id = [1, raw.aid, raw.id];
                                    obj.$_parent_id = Identifier.study_pack({ admin_id: raw.aid });
                                    obj.spec = "lamp.survey";
                                    obj.name = raw.name;
                                    obj.settings = raw.questions;
                                }
                                return obj;
                            })];
                }
            });
        });
    };
    Activity._upsert = function (documents) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                documents = documents.filter(function (x) { return x.spec === "lamp.survey"; });
                // TODO... use schedule + settings for survey config!
                // TODO: ActivitySpec::_jewelsMap('key', null)
                // ... use name for rename activity only
                // ... use schedule + settings for survey config!
                // UPDATE Survey SET IsDeleted = 1 WHERE SurveyID = ${id.survey_id};
                throw new Error();
            });
        });
    };
    return Activity;
}());
exports.Activity = Activity;
var ActivityEvent = /** @class */ (function () {
    function ActivityEvent() {
    }
    ActivityEvent._select = function (change_version) {
        return __awaiter(this, void 0, void 0, function () {
            var id, activity_id_or_spec, from_date, to_date, user_id, admin_id, conds, str, result, _a, _b, _c;
            var _d;
            var _this = this;
            return __generator(this, function (_e) {
                switch (_e.label) {
                    case 0:
                        id = undefined;
                        activity_id_or_spec = undefined;
                        from_date = undefined;
                        to_date = undefined;
                        if (!!id && Identifier.unpack(id)[0] === Researcher.name)
                            admin_id = Identifier.researcher_unpack(id).admin_id;
                        else if (!!id && Identifier.unpack(id).length === 0 /* Participant */)
                            user_id = Identifier.participant_unpack(id).study_id;
                        else if (!!id)
                            throw new Error();
                        user_id = !!user_id ? exports.Encrypt(user_id) : user_id;
                        conds = [
                            !!user_id ? "Users.StudyId = '" + user_id + "'" : null,
                            !!admin_id ? "Users.AdminID = '" + admin_id + "'" : null,
                            !!from_date ? "DATEDIFF_BIG(MS, '1970-01-01', timestamp) >= " + from_date : null,
                            !!to_date ? "DATEDIFF_BIG(MS, '1970-01-01', timestamp) <= " + to_date : null,
                        ].filter(function (x) { return !!x; });
                        str = conds.length > 0 ? "WHERE " + conds.join(" AND ") : "";
                        return [4 /*yield*/, SQL.request().query("\n\t\t\tSELECT * FROM LAMP_Aux.dbo.ActivityIndex;\n\t\t")];
                    case 1:
                        result = (_e.sent()).recordset.map(function (entry) { return __awaiter(_this, void 0, void 0, function () {
                            var events, slices, res, _a, _b, _c;
                            var _d;
                            var _this = this;
                            return __generator(this, function (_e) {
                                switch (_e.label) {
                                    case 0: return [4 /*yield*/, SQL.request().query("\n\t\t\t\tSELECT\n\t\t\t\t\tUsers.StudyId AS uid,\n\t                [" + entry.IndexColumnName + "] AS id,\n\t                DATEDIFF_BIG(MS, '1970-01-01', [" + entry.StartTimeColumnName + "]) AS timestamp,\n\t                DATEDIFF_BIG(MS, [" + entry.StartTimeColumnName + "], [" + entry.EndTimeColumnName + "]) AS duration,\n\t                " + (!entry.Slot1Name ? "" : "[" + entry.Slot1ColumnName + "] AS [static_data." + entry.Slot1Name + "],") + "\n\t                " + (!entry.Slot2Name ? "" : "[" + entry.Slot2ColumnName + "] AS [static_data." + entry.Slot2Name + "],") + "\n\t                " + (!entry.Slot3Name ? "" : "[" + entry.Slot3ColumnName + "] AS [static_data." + entry.Slot3Name + "],") + "\n\t                " + (!entry.Slot4Name ? "" : "[" + entry.Slot4ColumnName + "] AS [static_data." + entry.Slot4Name + "],") + "\n\t                " + (!entry.Slot5Name ? "" : "[" + entry.Slot5ColumnName + "] AS [static_data." + entry.Slot5Name + "],") + "\n\t                Users.AdminID AS aid\n\t            FROM [" + entry.TableName + "]\n\t            LEFT JOIN Users\n\t                ON [" + entry.TableName + "].UserID = Users.UserID\n\t            " + str.replace(/timestamp/g, "[" + entry.StartTimeColumnName + "]") + "\n\t            " + (!!change_version
                                            ? (conds.length === 0 ? "WHERE" : "AND") + " [" + entry.IndexColumnName + "] IN (\n            \t\tSELECT C.[" + entry.IndexColumnName + "] \n            \t\tFROM CHANGETABLE(CHANGES [" + entry.TableName + "], " + change_version + ") AS C \n            \t\tWHERE SYS_CHANGE_OPERATION = 'I' \n            \t\t\tAND SYS_CHANGE_CONTEXT IS NULL\n    \t\t\t)"
                                            : "") + "\n\t\t\t;")];
                                    case 1:
                                        events = (_e.sent()).recordset;
                                        if (events.length === 0)
                                            return [2 /*return*/, []
                                                // If temporal events are recorded by the activity, look all of them up as well.
                                            ];
                                        slices = [];
                                        if (!!!entry.TemporalTableName) return [3 /*break*/, 3];
                                        return [4 /*yield*/, SQL.request().query("\n\t                SELECT\n\t                    [" + entry.TemporalTableName + "].[" + entry.IndexColumnName + "] AS parent_id,\n\t                    " + (!!entry.Temporal1ColumnName
                                                ? "[" + entry.TemporalTableName + "].[" + entry.Temporal1ColumnName + "]"
                                                : "(NULL)") + " AS item,\n\t                    " + (!!entry.Temporal2ColumnName
                                                ? "[" + entry.TemporalTableName + "].[" + entry.Temporal2ColumnName + "]"
                                                : "(NULL)") + " AS value,\n\t                    " + (!!entry.Temporal3ColumnName
                                                ? "[" + entry.TemporalTableName + "].[" + entry.Temporal3ColumnName + "]"
                                                : "(NULL)") + " AS type,\n\t                    " + (!!entry.Temporal4ColumnName
                                                ? "CAST(CAST([" + entry.TemporalTableName + "].[" + entry.Temporal4ColumnName + "] AS float) * 1000 AS bigint)"
                                                : "(NULL)") + " AS duration,\n\t                    " + (!!entry.Temporal5ColumnName
                                                ? "[" + entry.TemporalTableName + "].[" + entry.Temporal5ColumnName + "]"
                                                : "(NULL)") + " AS level\n\t                FROM [" + entry.TemporalTableName + "]\n\t                LEFT JOIN [" + entry.TableName + "]\n\t                    ON [" + entry.TableName + "].[" + entry.IndexColumnName + "] = [" + entry.TemporalTableName + "].[" + entry.IndexColumnName + "]\n\t\t            LEFT JOIN Users\n\t\t                ON [" + entry.TableName + "].UserID = Users.UserID\n\t                " + str + "\n\t\t\t\t;")];
                                    case 2:
                                        slices = (_e.sent()).recordset;
                                        _e.label = 3;
                                    case 3:
                                        res = events.map(function (row) { return __awaiter(_this, void 0, void 0, function () {
                                            var activity_event, fname, fname;
                                            var _a, _b;
                                            return __generator(this, function (_c) {
                                                activity_event = {};
                                                activity_event.$_parent_id = exports.Decrypt(row.uid);
                                                activity_event.timestamp = (_a = Number.parse(row.timestamp), (_a !== null && _a !== void 0 ? _a : 0));
                                                activity_event.duration = (_b = Number.parse(row.duration), (_b !== null && _b !== void 0 ? _b : 0));
                                                // Map internal ID sub-components into the single mangled ID form.
                                                // FIXME: Currently it's not feasible to map SurveyID from SurveyName.
                                                activity_event.activity =
                                                    entry.ActivityIndexID === 1 /* survey */
                                                        ? undefined
                                                        : Identifier.activity_pack({ activity_spec_id: entry.ActivityIndexID, admin_id: row.aid, survey_id: 0 });
                                                // Copy static data fields if declared.
                                                activity_event.static_data = {};
                                                if (!!entry.Slot1ColumnName)
                                                    activity_event.static_data[entry.Slot1Name] = row["static_data." + entry.Slot1Name];
                                                if (!!entry.Slot2ColumnName)
                                                    activity_event.static_data[entry.Slot2Name] = row["static_data." + entry.Slot2Name];
                                                if (!!entry.Slot3ColumnName)
                                                    activity_event.static_data[entry.Slot3Name] = row["static_data." + entry.Slot3Name];
                                                if (!!entry.Slot4ColumnName)
                                                    activity_event.static_data[entry.Slot4Name] = row["static_data." + entry.Slot4Name];
                                                if (!!entry.Slot5ColumnName)
                                                    activity_event.static_data[entry.Slot5Name] = row["static_data." + entry.Slot5Name];
                                                // Decrypt all static data properties if known to be encrypted.
                                                // TODO: Encryption of fields should also be found in the ActivityIndex table!
                                                if (!!activity_event.static_data.survey_name)
                                                    activity_event.static_data.survey_name =
                                                        exports.Decrypt(activity_event.static_data.survey_name) || activity_event.static_data.survey_name;
                                                if (!!activity_event.static_data.drawn_fig_file_name) {
                                                    fname = "https://psych.digital/LampWeb/Games/User3DFigures/" +
                                                        (exports.Decrypt(activity_event.static_data.drawn_fig_file_name) || activity_event.static_data.drawn_fig_file_name);
                                                    activity_event.static_data.drawn_figure = fname; //(await Download(fname)).toString('base64')
                                                    activity_event.static_data.drawn_fig_file_name = undefined;
                                                }
                                                if (!!activity_event.static_data.scratch_file_name) {
                                                    fname = "https://psych.digital/LampWeb/Games/UserScratchImages/" +
                                                        (exports.Decrypt(activity_event.static_data.scratch_file_name) || activity_event.static_data.scratch_file_name);
                                                    activity_event.static_data.scratch_figure = fname; //(await Download(fname)).toString('base64')
                                                    activity_event.static_data.scratch_file_name = undefined;
                                                }
                                                if (!!activity_event.static_data.game_name)
                                                    activity_event.static_data.game_name =
                                                        exports.Decrypt(activity_event.static_data.game_name) || activity_event.static_data.game_name;
                                                if (!!activity_event.static_data.collected_stars)
                                                    activity_event.static_data.collected_stars =
                                                        exports.Decrypt(activity_event.static_data.collected_stars) || activity_event.static_data.collected_stars;
                                                if (!!activity_event.static_data.total_jewels_collected)
                                                    activity_event.static_data.total_jewels_collected =
                                                        exports.Decrypt(activity_event.static_data.total_jewels_collected) ||
                                                            activity_event.static_data.total_jewels_collected;
                                                if (!!activity_event.static_data.total_bonus_collected)
                                                    activity_event.static_data.total_bonus_collected =
                                                        exports.Decrypt(activity_event.static_data.total_bonus_collected) ||
                                                            activity_event.static_data.total_bonus_collected;
                                                if (!!activity_event.static_data.score)
                                                    activity_event.static_data.score =
                                                        exports.Decrypt(activity_event.static_data.score) || activity_event.static_data.score;
                                                // Copy all temporal events for this result event by matching parent ID.
                                                if (!!slices) {
                                                    activity_event.temporal_slices = slices
                                                        .filter(function (slice_row) { return slice_row.parent_id === row.id; })
                                                        .map(function (slice_row) {
                                                        var _a;
                                                        var temporal_slice = {};
                                                        temporal_slice.item = slice_row.item;
                                                        temporal_slice.value = slice_row.value;
                                                        temporal_slice.type = slice_row.type;
                                                        temporal_slice.duration = (_a = Number.parse(slice_row.duration), (_a !== null && _a !== void 0 ? _a : 0));
                                                        temporal_slice.level = slice_row.level;
                                                        // Special treatment for surveys with encrypted answers.
                                                        if (entry.ActivityIndexID === "1" /* survey */) {
                                                            temporal_slice.item = exports.Decrypt(temporal_slice.item) || temporal_slice.item;
                                                            temporal_slice.value = exports.Decrypt(temporal_slice.value) || temporal_slice.value;
                                                            temporal_slice.type = !temporal_slice.type ? undefined : temporal_slice.type.toLowerCase();
                                                            // Adjust the Likert scaled values to numbers.
                                                            if (["Not at all", "12:00AM - 06:00AM", "0-3"].indexOf(temporal_slice.value) >= 0) {
                                                                temporal_slice.value = 0;
                                                            }
                                                            else if (["Several Times", "06:00AM - 12:00PM", "3-6"].indexOf(temporal_slice.value) >= 0) {
                                                                temporal_slice.value = 1;
                                                            }
                                                            else if (["More than Half the Time", "12:00PM - 06:00PM", "6-9"].indexOf(temporal_slice.value) >= 0) {
                                                                temporal_slice.value = 2;
                                                            }
                                                            else if (["Nearly All the Time", "06:00PM - 12:00AM", ">9"].indexOf(temporal_slice.value) >= 0) {
                                                                temporal_slice.value = 3;
                                                            }
                                                        }
                                                        return temporal_slice;
                                                    });
                                                }
                                                // Finally return the newly created event.
                                                return [2 /*return*/, activity_event];
                                            });
                                        }); });
                                        _b = (_a = (_d = []).concat).apply;
                                        _c = [_d];
                                        return [4 /*yield*/, Promise.all(res)];
                                    case 4: return [2 /*return*/, _b.apply(_a, _c.concat([__spread.apply(void 0, [(_e.sent())])]))];
                                }
                            });
                        }); });
                        _b = (_a = (_d = []).concat).apply;
                        _c = [_d];
                        return [4 /*yield*/, Promise.all(result)];
                    case 2: return [2 /*return*/, _b.apply(_a, _c.concat([__spread.apply(void 0, [(_e.sent())])]))];
                }
            });
        });
    };
    ActivityEvent._upsert = function (documents) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                throw new Error("unsupported");
            });
        });
    };
    return ActivityEvent;
}());
exports.ActivityEvent = ActivityEvent;
var SensorEvent = /** @class */ (function () {
    function SensorEvent() {
    }
    SensorEvent._select = function (change_version) {
        return __awaiter(this, void 0, void 0, function () {
            var id, activity_id_or_spec, from_date, to_date, _decrypt, _convert, _clean, toLAMP, fromLAMP, HK_to_LAMP, LAMP_to_HK, HK_LAMP_map, SensorName, user_id, admin_id, result1, result2;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        id = undefined;
                        activity_id_or_spec = undefined;
                        from_date = undefined;
                        to_date = undefined;
                        _decrypt = function (str) {
                            var v = exports.Decrypt(str);
                            return !v || v === "" || v === "NA" ? null : v.toLowerCase();
                        };
                        _convert = function (x, strip_suffix, convert_number) {
                            if (strip_suffix === void 0) { strip_suffix = ""; }
                            if (convert_number === void 0) { convert_number = false; }
                            return !x ? null : convert_number ? parseFloat(x.replace(strip_suffix, "")) : x.replace(strip_suffix, "");
                        };
                        _clean = function (x) {
                            return x === 0 ? null : x;
                        };
                        toLAMP = function (value) {
                            if (!value)
                                return [];
                            var matches = (exports.Decrypt(value) || value).toLowerCase().match(/(?:i am )([ \S\/]+)(alone|in [ \S\/]*|with [ \S\/]*)/) || [];
                            return [
                                {
                                    home: "home",
                                    "in school/class": "school",
                                    "at work": "work",
                                    "in clinic/hospital": "hospital",
                                    outside: "outside",
                                    "shopping/dining": "shopping",
                                    "in bus/train/car": "transit",
                                }[(matches[1] || " ").slice(0, -1)],
                                {
                                    alone: "alone",
                                    "with friends": "friends",
                                    "with family": "family",
                                    "with peers": "peers",
                                    "in crowd": "crowd",
                                }[matches[2] || ""],
                            ];
                        };
                        fromLAMP = function (value) {
                            if (!value[0] && !value[1])
                                return undefined;
                            return exports.Encrypt("i am" +
                                {
                                    home: " home",
                                    school: " in school/class",
                                    work: " at work",
                                    hospital: " in clinic/hospital",
                                    outside: " outside",
                                    shopping: " shopping/dining",
                                    transit: " in bus/train/car",
                                }[value[0] || ""] +
                                {
                                    alone: "alone",
                                    friends: "with friends",
                                    family: "with family",
                                    peers: "with peers",
                                    crowd: "in crowd",
                                }[value[1] || ""]);
                        };
                        HK_to_LAMP = {
                            "lamp.height": function (raw) { return ({ value: _convert(_decrypt(raw), " cm", true), units: "cm" }); },
                            "lamp.weight": function (raw) { return ({ value: _convert(_decrypt(raw), " kg", true), units: "kg" }); },
                            "lamp.heart_rate": function (raw) { return ({ value: _convert(_decrypt(raw), " bpm", true), units: "bpm" }); },
                            "lamp.blood_pressure": function (raw) { return ({ value: _convert(_decrypt(raw), " mmhg", false), units: "mmHg" }); },
                            "lamp.respiratory_rate": function (raw) { return ({
                                value: _convert(_decrypt(raw), " breaths/min", true),
                                units: "bpm",
                            }); },
                            "lamp.sleep": function (raw) { return ({ value: _decrypt(raw), units: "" }); },
                            "lamp.steps": function (raw) { return ({ value: _clean(_convert(_decrypt(raw), " steps", true)), units: "steps" }); },
                            "lamp.flights": function (raw) { return ({
                                value: _clean(_convert(_decrypt(raw), " steps", true)),
                                units: "flights",
                            }); },
                            "lamp.segment": function (raw) { return ({ value: _convert(_decrypt(raw), "", true), units: "" }); },
                            "lamp.distance": function (raw) { return ({ value: _convert(_decrypt(raw), " meters", true), units: "meters" }); },
                        };
                        LAMP_to_HK = {
                            // TODO: Consider 0/NA values
                            "lamp.height": function (obj) { return exports.Encrypt(obj.value) + " cm"; },
                            "lamp.weight": function (obj) { return exports.Encrypt(obj.value) + " kg"; },
                            "lamp.heart_rate": function (obj) { return exports.Encrypt(obj.value) + " bpm"; },
                            "lamp.blood_pressure": function (obj) { return exports.Encrypt(obj.value) + " mmhg"; },
                            "lamp.respiratory_rate": function (obj) { return exports.Encrypt(obj.value) + " breaths/min"; },
                            "lamp.sleep": function (obj) { return "" + exports.Encrypt(obj.value); },
                            "lamp.steps": function (obj) { return exports.Encrypt(obj.value) + " steps"; },
                            "lamp.flights": function (obj) { return exports.Encrypt(obj.value) + " steps"; },
                            "lamp.segment": function (obj) { return "" + exports.Encrypt(obj.value); },
                            "lamp.distance": function (obj) { return exports.Encrypt(obj.value) + " meters"; },
                        };
                        HK_LAMP_map = {
                            "lamp.height": "Height",
                            "lamp.weight": "Weight",
                            "lamp.heart_rate": "HeartRate",
                            "lamp.blood_pressure": "BloodPressure",
                            "lamp.respiratory_rate": "RespiratoryRate",
                            "lamp.sleep": "Sleep",
                            "lamp.steps": "Steps",
                            "lamp.flights": "FlightClimbed",
                            "lamp.segment": "Segment",
                            "lamp.distance": "Distance",
                            Height: "lamp.height",
                            Weight: "lamp.weight",
                            HeartRate: "lamp.heart_rate",
                            BloodPressure: "lamp.blood_pressure",
                            RespiratoryRate: "lamp.respiratory_rate",
                            Sleep: "lamp.sleep",
                            Steps: "lamp.steps",
                            FlightClimbed: "lamp.flights",
                            Segment: "lamp.segment",
                            Distance: "lamp.distance",
                        };
                        (function (SensorName) {
                            SensorName["Analytics"] = "lamp.analytics";
                            SensorName["Accelerometer"] = "lamp.accelerometer";
                            SensorName["Bluetooth"] = "lamp.bluetooth";
                            SensorName["Calls"] = "lamp.calls";
                            SensorName["DeviceState"] = "lamp.device_state";
                            SensorName["SMS"] = "lamp.sms";
                            SensorName["WiFi"] = "lamp.wifi";
                            SensorName["Audio"] = "lamp.audio_recordings";
                            SensorName["Location"] = "lamp.gps";
                            SensorName["ContextualLocation"] = "lamp.gps.contextual";
                            SensorName["Height"] = "lamp.height";
                            SensorName["Weight"] = "lamp.weight";
                            SensorName["HeartRate"] = "lamp.heart_rate";
                            SensorName["BloodPressure"] = "lamp.blood_pressure";
                            SensorName["RespiratoryRate"] = "lamp.respiratory_rate";
                            SensorName["Sleep"] = "lamp.sleep";
                            SensorName["Steps"] = "lamp.steps";
                            SensorName["Flights"] = "lamp.flights";
                            SensorName["Segment"] = "lamp.segment";
                            SensorName["Distance"] = "lamp.distance";
                        })(SensorName || (SensorName = {}));
                        if (!!id && Identifier.unpack(id)[0] === Researcher.name)
                            admin_id = Identifier.researcher_unpack(id).admin_id;
                        else if (!!id && Identifier.unpack(id).length === 0 /* Participant */)
                            user_id = Identifier.participant_unpack(id).study_id;
                        else if (!!id)
                            throw new Error();
                        user_id = !!user_id ? exports.Encrypt(user_id) : undefined;
                        return [4 /*yield*/, SQL.request().query("\n\t\t\t\tSELECT timestamp, type, data, X.StudyId AS parent\n\t\t\t\tFROM (\n\t\t\t\t\tSELECT\n\t\t\t\t\t\tUsers.AdminID, \n\t\t\t\t\t\tUsers.StudyId, \n\t\t\t\t\t\tUsers.IsDeleted,\n\t\t\t\t\t\tDATEDIFF_BIG(MS, '1970-01-01', U.CreatedOn) AS timestamp, \n\t\t\t\t\t\tU.type,\n\t\t\t\t\t\tU.data\n\t\t\t\t\tFROM HealthKit_DailyValues\n\t\t\t\t\tUNPIVOT (data FOR type IN (\n\t\t\t\t\t\tHeight, Weight, HeartRate, BloodPressure, \n\t\t\t\t\t\tRespiratoryRate, Sleep, Steps, FlightClimbed, \n\t\t\t\t\t\tSegment, Distance\n\t\t\t\t\t)) U\n\t\t\t\t\tLEFT JOIN Users\n\t\t\t\t\t    ON U.UserID = Users.UserID\n\t\t\t\t\tWHERE U.data != ''\n                \t" + (!!change_version ? "AND 1=0" : "") + "\n\t\t\t\t\tUNION ALL \n\t\t\t\t\tSELECT\n\t\t\t\t\t\tUsers.AdminID, \n\t\t\t\t\t\tUsers.StudyId, \n\t\t\t\t\t\tUsers.IsDeleted,\n\t\t\t\t\t    DATEDIFF_BIG(MS, '1970-01-01', DateTime) AS timestamp,\n\t\t\t\t\t    REPLACE(HKParamName, ' ', '') AS type,\n\t\t\t\t\t    Value AS data\n\t\t\t\t\tFROM HealthKit_ParamValues\n\t\t\t\t\tLEFT JOIN Users\n\t\t\t\t\t    ON HealthKit_ParamValues.UserID = Users.UserID\n\t\t\t\t\tLEFT JOIN HealthKit_Parameters\n\t\t\t\t\t    ON HealthKit_Parameters.HKParamID = HealthKit_ParamValues.HKParamID\n                \t" + (!!change_version
                                ? "WHERE HKParamValueID IN (SELECT C.HKParamValueID FROM CHANGETABLE(CHANGES HealthKit_ParamValues, " + change_version + ") AS C WHERE SYS_CHANGE_OPERATION = 'I' AND SYS_CHANGE_CONTEXT IS NULL)"
                                : "") + "\n\t\t\t\t) X\n\t\t\t\tWHERE 1=1\n                " + (!!user_id ? "AND X.StudyId = '" + user_id + "'" : "") + "\n                " + (!!admin_id ? "AND X.AdminID = '" + admin_id + "'" : "") + "\n                " + (!!from_date ? "AND X.timestamp >= " + from_date : "") + "\n                " + (!!to_date ? "AND X.timestamp <= " + to_date : "") + "\n\t\t;")];
                    case 1:
                        result1 = (_a.sent()).recordset.map(function (raw) {
                            var obj = {};
                            obj.$_parent_id = exports.Decrypt(raw.parent);
                            obj.timestamp = raw.timestamp;
                            obj.sensor = Object.entries(HK_LAMP_map).filter(function (x) { return x[1] === raw.type; })[0][0];
                            obj.data = (HK_to_LAMP[obj.sensor] || (function (x) { return x; }))(raw.data);
                            return obj;
                        });
                        return [4 /*yield*/, SQL.request().query("\n\t\t\tSELECT \n                DATEDIFF_BIG(MS, '1970-01-01', Locations.CreatedOn) AS timestamp,\n                Latitude AS lat,\n                Longitude AS long,\n                LocationName AS location_name,\n                Users.StudyId AS parent\n            FROM Locations\n            LEFT JOIN Users\n                ON Locations.UserID = Users.UserID\n            WHERE 1=1\n                " + (!!user_id ? "AND Users.StudyId = '" + user_id + "'" : "") + "\n                " + (!!admin_id ? "AND Users.AdminID = '" + admin_id + "'" : "") + "\n                " + (!!from_date ? "AND DATEDIFF_BIG(MS, '1970-01-01', Locations.CreatedOn) >= " + from_date : "") + "\n                " + (!!to_date ? "AND DATEDIFF_BIG(MS, '1970-01-01', Locations.CreatedOn) <= " + to_date : "") + "\n                " + (!!change_version
                                ? "AND LocationID IN (SELECT C.LocationID FROM CHANGETABLE(CHANGES Locations, " + change_version + ") AS C WHERE SYS_CHANGE_OPERATION = 'I' AND SYS_CHANGE_CONTEXT IS NULL)"
                                : "") + "\n\t\t;")];
                    case 2:
                        result2 = (_a.sent()).recordset.map(function (raw) {
                            var x = toLAMP(raw.location_name);
                            var obj = {};
                            obj.$_parent_id = exports.Decrypt(raw.parent);
                            obj.timestamp = raw.timestamp;
                            obj.sensor = "lamp.gps.contextual";
                            obj.data = {
                                latitude: parseFloat(exports.Decrypt(raw.lat) || raw.lat),
                                longitude: parseFloat(exports.Decrypt(raw.long) || raw.long),
                                accuracy: 1,
                                context: {
                                    environment: x[0] || null,
                                    social: x[1] || null,
                                },
                            };
                            return obj;
                        });
                        return [2 /*return*/, __spread(result1, result2).sort(function (a, b) { return a.timestamp - b.timestamp; })];
                }
            });
        });
    };
    SensorEvent._upsert = function (documents) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                throw new Error("unsupported");
            });
        });
    };
    return SensorEvent;
}());
exports.SensorEvent = SensorEvent;
var Credential = /** @class */ (function () {
    function Credential() {
    }
    Credential._select = function (change_version) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                throw new Error("unsupported");
            });
        });
    };
    Credential._upsert = function (documents) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                throw new Error("unsupported");
            });
        });
    };
    return Credential;
}());
exports.Credential = Credential;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoibWlncmF0b3JzLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vc3JjL3V0aWxzL21pZ3JhdG9yL21pZ3JhdG9ycy50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7QUFDQSxrREFBMkI7QUFHM0IsSUFBSSxHQUFtQyxDQUFBO0FBRXZDOzs7RUFHRTtBQUVGOzs7RUFHRTtBQUVGOztFQUVFO0FBRUY7Ozs7O0VBS0U7QUFFVyxRQUFBLEtBQUssR0FBRyxVQUFPLEdBQVc7Ozs7S0FFdEMsQ0FBQTtBQUVZLFFBQUEsT0FBTyxHQUFHOzs7Ozs7Z0JBRW5CLEtBQUEsRUFBRSxDQUFBO2dCQUFJLEtBQUEsQ0FBQSxLQUFBLE1BQU0sQ0FBQSxDQUFDLEtBQUssQ0FBQTtnQkFBRSxxQkFBTSxHQUFJLENBQUMsT0FBTyxFQUFFLENBQUMsS0FBSyxDQUFDLDJDQUEyQyxDQUFDLEVBQUE7b0JBRDdGLHNCQUFPLENBQ0wsS0FBSyxNQUFDLGNBQWEsQ0FBQyxTQUF1RSxDQUFDLENBQUMsU0FBUyxDQUFDLENBQUMsQ0FBQyxDQUFDLEVBQUUsQ0FBQyxFQUFDLHVDQUFJLENBQUMsRUFBQyxDQUNySCxFQUFBOzs7S0FDRixDQUFBO0FBRVksUUFBQSxPQUFPLEdBQUcsVUFBQyxJQUFZLEVBQUUsSUFBd0M7SUFBeEMscUJBQUEsRUFBQSxpQkFBd0M7SUFDNUUsSUFBSTtRQUNGLElBQUksSUFBSSxLQUFLLFVBQVUsRUFBRTtZQUN2QixJQUFNLE1BQU0sR0FBRyxnQkFBTSxDQUFDLGNBQWMsQ0FBQyxhQUFhLEVBQUUsT0FBTyxDQUFDLEdBQUcsQ0FBQyxNQUFNLElBQUksRUFBRSxFQUFFLEVBQUUsQ0FBQyxDQUFBO1lBQ2pGLE9BQU8sTUFBTSxDQUFDLE1BQU0sQ0FBQyxJQUFJLEVBQUUsTUFBTSxFQUFFLFFBQVEsQ0FBQyxHQUFHLE1B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