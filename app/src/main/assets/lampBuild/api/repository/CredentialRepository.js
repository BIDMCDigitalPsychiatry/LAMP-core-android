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
Object.defineProperty(exports, "__esModule", { value: true });
var app_1 = require("../app");
var Researcher_1 = require("../model/Researcher");
var ResearcherRepository_1 = require("../repository/ResearcherRepository");
var ParticipantRepository_1 = require("../repository/ParticipantRepository");
var TypeRepository_1 = require("../repository/TypeRepository");
// TODO: Credential.delete -> promote tag credential to legacy credential to allow login!
var CredentialRepository = /** @class */ (function () {
    function CredentialRepository() {
    }
    // DANGER: This decrypts and dumps EVERY SINGLE CREDENTIAL!!! DO NOT USE EXCEPT FOR DEBUGGING!
    CredentialRepository._showAll = function () {
        return __awaiter(this, void 0, void 0, function () {
            var result1, out1, result2, out2, result3, out3;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tSELECT AdminID, Email, Password\n\t\t\tFROM Admin\n\t\t\tWHERE IsDeleted = 0\n\t\t;")];
                    case 1:
                        result1 = _a.sent();
                        out1 = result1.recordset.map(function (x) {
                            var _a;
                            return ({
                                origin: ResearcherRepository_1.ResearcherRepository._pack_id({ admin_id: (_a = Number.parse(x["AdminID"]), (_a !== null && _a !== void 0 ? _a : 0)) }),
                                access_key: app_1.Decrypt(x["Email"]),
                                secret_key: app_1.Decrypt(x["Password"], "AES256"),
                                description: "Default Credential",
                            });
                        });
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tSELECT StudyId, Email, Password\n\t\t\tFROM Users\n\t\t\tWHERE IsDeleted = 0\n\t\t;")];
                    case 2:
                        result2 = _a.sent();
                        out2 = result2.recordset.map(function (x) { return ({
                            origin: app_1.Decrypt(x["StudyId"]),
                            access_key: app_1.Decrypt(x["Email"]),
                            secret_key: app_1.Decrypt(x["Password"], "AES256"),
                            description: "Default Credential",
                        }); });
                        return [4 /*yield*/, app_1.SQL.request().query("\n            SELECT ObjectID, Value\n            FROM LAMP_Aux.dbo.OOLAttachment\n            WHERE ObjectType = 'Credential'\n\t\t;")];
                    case 3:
                        result3 = _a.sent();
                        out3 = result3.recordset
                            .map(function (x) { return JSON.parse(x["Value"]); })
                            .map(function (x) { return (__assign(__assign({}, x), { secret_key: app_1.Decrypt(x.secret_key, "AES256") })); });
                        return [2 /*return*/, __spread(out1, out2, out3)];
                }
            });
        });
    };
    // if used with secret_key, will throw error if mismatch, else, will return confirmation of existence
    CredentialRepository._find = function (access_key, secret_key) {
        var _a;
        return __awaiter(this, void 0, void 0, function () {
            var result;
            return __generator(this, function (_b) {
                switch (_b.label) {
                    case 0:
                        result = null;
                        return [4 /*yield*/, app_1.SQL.request().query("\n            SELECT ObjectID, Value\n            FROM LAMP_Aux.dbo.OOLAttachment\n            WHERE (\n                \t[Key] = '" + access_key + "'\n                \tAND ObjectType = 'Credential'\n                )\n\t\t;")];
                    case 1:
                        // Get any API credentials.
                        // Note: THIS MUST HAPPEN FIRST! If not, you will see email address conflicts with legacy accounts.
                        result = _b.sent();
                        if (result.rowsAffected[0] > 0) {
                            if (!!secret_key && secret_key !== app_1.Decrypt(JSON.parse(result.recordset[0]["Value"])["secret_key"], "AES256"))
                                throw new Error("403.no-such-credentials");
                            return [2 /*return*/, result.recordset[0]["ObjectID"]];
                        }
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tSELECT AdminID, Password\n\t\t\tFROM Admin\n\t\t\tWHERE IsDeleted = 0 \n\t\t\t\tAND Email = '" + app_1.Encrypt(access_key) + "'\n\t\t\t\tAND (Password IS NOT NULL AND Password != '')\n\t\t;")];
                    case 2:
                        // Reset the legacy/default credential as a Researcher.
                        result = _b.sent();
                        if (result.rowsAffected[0] > 0) {
                            if (!!secret_key && secret_key !== app_1.Decrypt(result.recordset[0]["Password"], "AES256"))
                                throw new Error("403.no-such-credentials");
                            return [2 /*return*/, ResearcherRepository_1.ResearcherRepository._pack_id({ admin_id: (_a = Number.parse(result.recordset[0]["AdminID"]), (_a !== null && _a !== void 0 ? _a : 0)) })];
                        }
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tSELECT Email, StudyId, Password\n\t\t\tFROM Users\n\t\t\tWHERE IsDeleted = 0 \n\t\t\t\tAND Email = '" + app_1.Encrypt(access_key) + "'\n\t\t\t\tAND (Password IS NOT NULL AND Password != '')\n\t\t;")];
                    case 3:
                        // Reset the legacy/default credential as a Participant.
                        result = _b.sent();
                        if (result.rowsAffected[0] > 0) {
                            if (!!secret_key && secret_key !== app_1.Decrypt(result.recordset[0]["Password"], "AES256"))
                                throw new Error("403.no-such-credentials");
                            return [2 /*return*/, app_1.Decrypt(result.recordset[0]["StudyId"])];
                        }
                        throw new Error("403.no-such-credentials");
                }
            });
        });
    };
    CredentialRepository._select = function (type_id) {
        return __awaiter(this, void 0, void 0, function () {
            var user_id, admin_id, legacy_key, result_1, result_2, result;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!!type_id && TypeRepository_1.Identifier_unpack(type_id)[0] === Researcher_1.Researcher.name)
                            admin_id = ResearcherRepository_1.ResearcherRepository._unpack_id(type_id).admin_id;
                        else if (!!type_id && TypeRepository_1.Identifier_unpack(type_id).length === 0 /* Participant */)
                            user_id = ParticipantRepository_1.ParticipantRepository._unpack_id(type_id).study_id;
                        else if (!!type_id)
                            throw new Error("400.invalid-identifier");
                        legacy_key = undefined;
                        if (!!!admin_id) return [3 /*break*/, 2];
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\t\tSELECT Email\n\t\t\t\tFROM Admin\n\t\t\t\tWHERE IsDeleted = 0 \n\t\t\t\t\tAND (Password IS NOT NULL AND Password != '')\n\t\t\t\t\tAND AdminID = " + admin_id + "\n\t\t\t;")];
                    case 1:
                        result_1 = _a.sent();
                        if (result_1.rowsAffected[0] > 0)
                            legacy_key = {
                                origin: type_id,
                                access_key: app_1.Decrypt(result_1.recordset[0]["Email"]) || "",
                                secret_key: null,
                                description: "Default Credential",
                            };
                        return [3 /*break*/, 4];
                    case 2:
                        if (!!!user_id) return [3 /*break*/, 4];
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\t\tSELECT Email\n\t\t\t\tFROM Users\n\t\t\t\tWHERE IsDeleted = 0 \n\t\t\t\t\tAND (Password IS NOT NULL AND Password != '')\n\t\t\t\t\tAND StudyId = '" + app_1.Encrypt(user_id) + "'\n\t\t\t;")];
                    case 3:
                        result_2 = _a.sent();
                        if (result_2.rowsAffected[0] > 0)
                            legacy_key = {
                                origin: type_id,
                                access_key: app_1.Decrypt(result_2.recordset[0]["Email"]) || "",
                                secret_key: null,
                                description: "Default Credential",
                            };
                        _a.label = 4;
                    case 4: return [4 /*yield*/, app_1.SQL.request().query("\n            SELECT [Key], Value\n            FROM LAMP_Aux.dbo.OOLAttachment\n            WHERE (\n                \tObjectID = '" + type_id + "'\n                \tAND ObjectType = 'Credential'\n                )\n\t\t;")];
                    case 5:
                        result = (_a.sent()).recordset;
                        //
                        return [2 /*return*/, __spread([legacy_key], result.map(function (x) { return JSON.parse(x["Value"]); }).map(function (x) { return (__assign(__assign({}, x), { secret_key: null })); })).filter(function (x) { return !!x; })];
                }
            });
        });
    };
    CredentialRepository._insert = function (type_id, credential) {
        return __awaiter(this, void 0, void 0, function () {
            var user_id, admin_id, result_3, result_4, x, e_1, result_5, result_6, req, result;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!!type_id && TypeRepository_1.Identifier_unpack(type_id)[0] === Researcher_1.Researcher.name)
                            admin_id = ResearcherRepository_1.ResearcherRepository._unpack_id(type_id).admin_id;
                        else if (!!type_id && TypeRepository_1.Identifier_unpack(type_id).length === 0 /* Participant */)
                            user_id = ParticipantRepository_1.ParticipantRepository._unpack_id(type_id).study_id;
                        else if (!!type_id)
                            throw new Error("400.invalid-identifier");
                        if (!(typeof credential === "string")) return [3 /*break*/, 4];
                        credential = {
                            origin: type_id,
                            access_key: "",
                            secret_key: credential,
                        };
                        if (!!!admin_id) return [3 /*break*/, 2];
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\t\t\tSELECT Email FROM Admin WHERE IsDeleted = 0 AND AdminID = " + admin_id + "\n\t\t\t\t;")];
                    case 1:
                        result_3 = _a.sent();
                        credential.access_key = app_1.Decrypt(result_3.recordset[0]["Email"]);
                        return [3 /*break*/, 4];
                    case 2:
                        if (!!!user_id) return [3 /*break*/, 4];
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\t\t\tSELECT Email FROM Users WHERE IsDeleted = 0 AND StudyId = '" + app_1.Encrypt(user_id) + "'\n\t\t\t\t;")];
                    case 3:
                        result_4 = _a.sent();
                        credential.access_key = app_1.Decrypt(result_4.recordset[0]["Email"]);
                        _a.label = 4;
                    case 4:
                        // HOTFIX ONLY!
                        if (credential.origin === "me") {
                            // context substitution doesn't actually work within the object here, so do it manually.
                            credential.origin = type_id;
                        }
                        // If it's not our credential, don't mess with it!
                        if (credential.origin !== type_id || !credential.access_key || !credential.secret_key)
                            throw new Error("400.malformed-credential-object");
                        _a.label = 5;
                    case 5:
                        _a.trys.push([5, 7, , 8]);
                        return [4 /*yield*/, CredentialRepository._find(credential.access_key)];
                    case 6:
                        x = _a.sent();
                        return [3 /*break*/, 8];
                    case 7:
                        e_1 = _a.sent();
                        return [3 /*break*/, 8];
                    case 8:
                        if (!!x)
                            throw new Error("403.access-key-already-in-use");
                        if (!!!admin_id) return [3 /*break*/, 10];
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\t\tUPDATE Admin \n\t\t\t\tSET \n\t\t\t\t\tEmail = '" + app_1.Encrypt(credential.access_key) + "',\n\t\t\t\t\tPassword = '" + app_1.Encrypt(credential.secret_key, "AES256") + "'\n\t\t\t\tWHERE IsDeleted = 0 \n\t\t\t\t\tAND (Password IS NULL OR Password = '')\n\t\t\t\t\tAND AdminID = " + admin_id + "\n\t\t\t;")];
                    case 9:
                        result_5 = _a.sent();
                        if (result_5.rowsAffected[0] > 0)
                            return [2 /*return*/, {}];
                        return [3 /*break*/, 12];
                    case 10:
                        if (!!!user_id) return [3 /*break*/, 12];
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\t\tUPDATE Users \n\t\t\t\tSET \n\t\t\t\t\tEmail = '" + app_1.Encrypt(credential.access_key) + "',\n\t\t\t\t\tPassword = '" + app_1.Encrypt(credential.secret_key, "AES256") + "'\n\t\t\t\tWHERE IsDeleted = 0 \n\t\t\t\t\tAND (Password IS NULL OR Password = '')\n\t\t\t\t\tAND StudyId = '" + app_1.Encrypt(user_id) + "'\n\t\t\t;")];
                    case 11:
                        result_6 = _a.sent();
                        if (result_6.rowsAffected[0] > 0)
                            return [2 /*return*/, {}];
                        _a.label = 12;
                    case 12:
                        // Reset an API credential as either a Researcher or Participant.
                        credential.secret_key = app_1.Encrypt(credential.secret_key, "AES256");
                        req = app_1.SQL.request();
                        req.input("json_value", JSON.stringify(credential));
                        return [4 /*yield*/, req.query("\n            INSERT INTO LAMP_Aux.dbo.OOLAttachment (\n                ObjectType, ObjectID, [Key], Value\n            )\n            VALUES (\n                'Credential', '" + type_id + "', '" + credential.access_key + "', @json_value\n            )\n\t\t;")];
                    case 13:
                        result = _a.sent();
                        if (result.rowsAffected[0] === 0)
                            throw new Error("404.object-not-found");
                        return [2 /*return*/, {}];
                }
            });
        });
    };
    CredentialRepository._update = function (type_id, access_key, credential) {
        return __awaiter(this, void 0, void 0, function () {
            var user_id, admin_id, result_7, result_8, req, result;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!!type_id && TypeRepository_1.Identifier_unpack(type_id)[0] === Researcher_1.Researcher.name)
                            admin_id = ResearcherRepository_1.ResearcherRepository._unpack_id(type_id).admin_id;
                        else if (!!type_id && TypeRepository_1.Identifier_unpack(type_id).length === 0 /* Participant */)
                            user_id = ParticipantRepository_1.ParticipantRepository._unpack_id(type_id).study_id;
                        else if (!!type_id)
                            throw new Error("400.invalid-identifier");
                        // If it's not our credential, don't mess with it!
                        //if (<string>(credential.origin) != <string>type_id)
                        //	throw new BadRequest("The credential origin does not match the requested resource.")
                        if (!credential.access_key || !credential.secret_key)
                            throw new Error("400.credential-requires-access-and-secret-keys");
                        if (!!!admin_id) return [3 /*break*/, 2];
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\t\tUPDATE Admin \n\t\t\t\tSET \n\t\t\t\t\tPassword = '" + app_1.Encrypt(credential.secret_key, "AES256") + "'\n\t\t\t\tWHERE IsDeleted = 0 \n\t\t\t\t\tAND (Password IS NOT NULL AND Password != '')\n\t\t\t\t\tAND Email = '" + app_1.Encrypt(credential.access_key) + "'\n\t\t\t\t\tAND AdminID = " + admin_id + "\n\t\t\t;")];
                    case 1:
                        result_7 = _a.sent();
                        if (result_7.rowsAffected[0] > 0)
                            return [2 /*return*/, {}];
                        return [3 /*break*/, 4];
                    case 2:
                        if (!!!user_id) return [3 /*break*/, 4];
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\t\tUPDATE Users \n\t\t\t\tSET \n\t\t\t\t\tPassword = '" + app_1.Encrypt(credential.secret_key, "AES256") + "'\n\t\t\t\tWHERE IsDeleted = 0 \n\t\t\t\t\tAND (Password IS NOT NULL AND Password != '')\n\t\t\t\t\tAND Email = '" + app_1.Encrypt(credential.access_key) + "'\n\t\t\t\t\tAND StudyId = '" + app_1.Encrypt(user_id) + "'\n\t\t\t;")];
                    case 3:
                        result_8 = _a.sent();
                        if (result_8.rowsAffected[0] > 0)
                            return [2 /*return*/, {}];
                        _a.label = 4;
                    case 4:
                        // Reset an API credential as either a Researcher or Participant.
                        credential.secret_key = app_1.Encrypt(credential.secret_key, "AES256");
                        req = app_1.SQL.request();
                        req.input("json_value", JSON.stringify(credential));
                        return [4 /*yield*/, req.query("\n            UPDATE LAMP_Aux.dbo.OOLAttachment SET\n\t            Value = @json_value\n            WHERE ObjectType = 'Credential'\n            \tAND ObjectID = '" + type_id + "'\n            \tAND [Key] = '" + credential.access_key + "'\n\t\t;")];
                    case 5:
                        result = _a.sent();
                        if (result.rowsAffected[0] === 0)
                            throw new Error("404.object-not-found");
                        return [2 /*return*/, {}];
                }
            });
        });
    };
    CredentialRepository._delete = function (type_id, access_key) {
        return __awaiter(this, void 0, void 0, function () {
            var user_id, admin_id, result_9, result_10, result;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!!type_id && TypeRepository_1.Identifier_unpack(type_id)[0] === Researcher_1.Researcher.name)
                            admin_id = ResearcherRepository_1.ResearcherRepository._unpack_id(type_id).admin_id;
                        else if (!!type_id && TypeRepository_1.Identifier_unpack(type_id).length === 0 /* Participant */)
                            user_id = ParticipantRepository_1.ParticipantRepository._unpack_id(type_id).study_id;
                        else if (!!type_id)
                            throw new Error("400.invalid-identifier");
                        if (!!!admin_id) return [3 /*break*/, 2];
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\t\tUPDATE Admin \n\t\t\t\tSET Password = '' \n\t\t\t\tWHERE IsDeleted = 0 \n\t\t\t\t\tAND Email = '" + app_1.Encrypt(access_key) + "'\n\t\t\t\t\tAND AdminID = " + admin_id + "\n\t\t\t\t\tAND (Password IS NOT NULL AND Password != '')\n\t\t\t;")];
                    case 1:
                        result_9 = _a.sent();
                        if (result_9.rowsAffected[0] > 0)
                            return [2 /*return*/, {}];
                        return [3 /*break*/, 4];
                    case 2:
                        if (!!!user_id) return [3 /*break*/, 4];
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\t\tUPDATE Users \n\t\t\t\tSET Password = '' \n\t\t\t\tWHERE IsDeleted = 0 \n\t\t\t\t\tAND Email = '" + app_1.Encrypt(access_key) + "'\n\t\t\t\t\tAND StudyId = '" + app_1.Encrypt(user_id) + "'\n\t\t\t\t\tAND (Password IS NOT NULL AND Password != '')\n\t\t\t;")];
                    case 3:
                        result_10 = _a.sent();
                        if (result_10.rowsAffected[0] > 0)
                            return [2 /*return*/, {}];
                        _a.label = 4;
                    case 4: return [4 /*yield*/, app_1.SQL.request().query("\n\t        DELETE FROM LAMP_Aux.dbo.OOLAttachment\n            WHERE \n                ObjectID = '" + type_id + "'\n                AND [Key] = '" + access_key + "'\n                AND ObjectType = 'Credential'\n\t\t;")];
                    case 5:
                        result = _a.sent();
                        if (result.rowsAffected[0] === 0)
                            throw new Error("404.access-key-not-found");
                        return [2 /*return*/, {}];
                }
            });
        });
    };
    return CredentialRepository;
}());
exports.CredentialRepository = CredentialRepository;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiQ3JlZGVudGlhbFJlcG9zaXRvcnkuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi9zcmMvcmVwb3NpdG9yeS9DcmVkZW50aWFsUmVwb3NpdG9yeS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7QUFDQSw4QkFBOEM7QUFJOUMsa0RBQWdEO0FBRWhELDJFQUF5RTtBQUN6RSw2RUFBMkU7QUFDM0UsK0RBQWlGO0FBRWpGLHlGQUF5RjtBQUV6RjtJQUFBO0lBOFdBLENBQUM7SUE3V0MsOEZBQThGO0lBQzFFLDZCQUFRLEdBQTVCOzs7Ozs0QkFFa0IscUJBQU0sU0FBSSxDQUFDLE9BQU8sRUFBRSxDQUFDLEtBQUssQ0FBQyw2RkFJM0MsQ0FBQyxFQUFBOzt3QkFKSyxPQUFPLEdBQUcsU0FJZjt3QkFFSyxJQUFJLEdBQUcsT0FBTyxDQUFDLFNBQVMsQ0FBQyxHQUFHLENBQUMsVUFBQyxDQUFDOzs0QkFBSyxPQUFBLENBQUM7Z0NBQ3pDLE1BQU0sRUFBRSwyQ0FBb0IsQ0FBQyxRQUFRLENBQUMsRUFBRSxRQUFRLFFBQUUsTUFBTSxDQUFDLEtBQUssQ0FBQyxDQUFDLENBQUMsU0FBUyxDQUFDLENBQUMsdUNBQUksQ0FBQyxFQUFBLEVBQUUsQ0FBQztnQ0FDcEYsVUFBVSxFQUFFLGFBQU8sQ0FBQyxDQUFDLENBQUMsT0FBTyxDQUFDLENBQUM7Z0NBQy9CLFVBQVUsRUFBRSxhQUFPLENBQUMsQ0FBQyxDQUFDLFVBQVUsQ0FBQyxFQUFFLFFBQVEsQ0FBQztnQ0FDNUMsV0FBVyxFQUFFLG9CQUFvQjs2QkFDbEMsQ0FBQyxDQUFBO3lCQUFBLENBQUMsQ0FBQTt3QkFHYSxxQkFBTSxTQUFJLENBQUMsT0FBTyxFQUFFLENBQUMsS0FBSyxDQUFDLDZGQUkzQyxDQUFDLEVBQUE7O3dCQUpLLE9BQU8sR0FBRyxTQUlmO3dCQUVLLElBQUks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