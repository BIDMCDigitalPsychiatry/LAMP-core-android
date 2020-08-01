"use strict";
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
Object.defineProperty(exports, "__esModule", { value: true });
var app_1 = require("../app");
var Study_1 = require("../model/Study");
var Researcher_1 = require("../model/Researcher");
var Participant_1 = require("../model/Participant");
var ResearcherRepository_1 = require("../repository/ResearcherRepository");
var StudyRepository_1 = require("../repository/StudyRepository");
var TypeRepository_1 = require("../repository/TypeRepository");
var ParticipantRepository = /** @class */ (function () {
    function ParticipantRepository() {
    }
    /**
     *
     */
    ParticipantRepository._pack_id = function (components) {
        return components.study_id || "";
    };
    /**
     *
     */
    ParticipantRepository._unpack_id = function (id) {
        return { study_id: id };
    };
    /**
     *
     */
    ParticipantRepository._parent_id = function (id, type) {
        return __awaiter(this, void 0, void 0, function () {
            var study_id, _a, result;
            return __generator(this, function (_b) {
                switch (_b.label) {
                    case 0:
                        study_id = ParticipantRepository._unpack_id(id).study_id;
                        _a = type;
                        switch (_a) {
                            case StudyRepository_1.StudyRepository: return [3 /*break*/, 1];
                            case ResearcherRepository_1.ResearcherRepository: return [3 /*break*/, 1];
                        }
                        return [3 /*break*/, 3];
                    case 1: return [4 /*yield*/, app_1.SQL.request().query("\n                    SELECT AdminID AS value\n                    FROM Users\n                    WHERE IsDeleted = 0 AND StudyId = '" + app_1.Encrypt(study_id) + "';\n\t\t\t\t")];
                    case 2:
                        result = (_b.sent()).recordset;
                        return [2 /*return*/, result.length === 0
                                ? undefined
                                : (type === ResearcherRepository_1.ResearcherRepository ? ResearcherRepository_1.ResearcherRepository : StudyRepository_1.StudyRepository)._pack_id({
                                    admin_id: result[0].value,
                                })];
                    case 3: throw new Error("400.invalid-identifier");
                }
            });
        });
    };
    /**
     * Get a set of `Participant`s matching the criteria parameters.
     */
    ParticipantRepository._select = function (
    /**
     *
     */
    id) {
        return __awaiter(this, void 0, void 0, function () {
            var user_id, admin_id, result;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!!id && TypeRepository_1.Identifier_unpack(id)[0] === Researcher_1.Researcher.name)
                            admin_id = ResearcherRepository_1.ResearcherRepository._unpack_id(id).admin_id;
                        else if (!!id && TypeRepository_1.Identifier_unpack(id)[0] === Study_1.Study.name)
                            admin_id = StudyRepository_1.StudyRepository._unpack_id(id).admin_id;
                        else if (!!id && TypeRepository_1.Identifier_unpack(id).length === 0 /* Participant */)
                            user_id = ParticipantRepository._unpack_id(id).study_id;
                        else if (!!id)
                            throw new Error("400.invalid-identifier");
                        return [4 /*yield*/, app_1.SQL.request().query("\n            SELECT \n                StudyId AS id, \n                StudyCode AS study_code, \n                AppColor AS [theme], \n                Language AS [language], \n                (\n                    SELECT [24By7ContactNo]\n                    WHERE [24By7ContactNo] != ''\n                ) AS [emergency_contact],\n                (\n                    SELECT PersonalHelpline\n                    WHERE PersonalHelpline != ''\n                ) AS [helpline]\n            FROM Users\n            FULL OUTER JOIN UserSettings\n                ON UserSettings.UserID = Users.UserID\n            FULL OUTER JOIN UserDevices\n                ON UserDevices.UserID = Users.UserID\n            WHERE \n            \tUsers.IsDeleted = 0 \n            \t" + (!!user_id ? "AND Users.StudyId = '" + app_1.Encrypt(user_id) + "'" : "") + " \n            \t" + (!!admin_id ? "AND Users.AdminID = '" + admin_id + "'" : "") + "\n            FOR JSON PATH, INCLUDE_NULL_VALUES;\n\t    ")];
                    case 1:
                        result = _a.sent();
                        if (result.recordset.length === 0 || !result.recordset[0])
                            return [2 /*return*/, []
                                // Map from SQL DB to the local Participant type.
                            ];
                        // Map from SQL DB to the local Participant type.
                        return [2 /*return*/, result.recordset[0].map(function (raw) {
                                var obj = new Participant_1.Participant();
                                obj.id = app_1.Decrypt(raw.id);
                                //obj.language = raw.language || "en"
                                //obj.theme = !!raw.theme ? Decrypt(raw.theme!) : undefined
                                //obj.emergency_contact = raw.emergency_contact
                                //obj.helpline = raw.helpline
                                return obj;
                            })];
                }
            });
        });
    };
    /**
     * Create a `Participant`.
     */
    ParticipantRepository._insert = function (
    /**
     * The `AdminID` column of the `Admin` table in the LAMP v0.1 DB.
     */
    study_id, 
    /**
     * The patch fields of the `Participant` object.
     */
    object) {
        return __awaiter(this, void 0, void 0, function () {
            var admin_id, _id, study_code, theme, language, emergency_contact, helpline, result1, result2;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        admin_id = StudyRepository_1.StudyRepository._unpack_id(study_id).admin_id;
                        _id = "U" + Math.random().toFixed(10).slice(2, 12);
                        study_code = !!object.study_code ? "'" + app_1.Encrypt(object.study_code) + "'" : "'" + app_1.Encrypt("001") + "'";
                        theme = !!object.theme ? "'" + app_1.Encrypt(object.theme) + "'" : "'dJjw5FK/FXK6qU32frXHvg=='";
                        language = !!object.language ? "'" + app_1.Encrypt(object.language) + "'" : "'en'";
                        emergency_contact = !!object.emergency_contact ? "'" + app_1.Encrypt(object.emergency_contact) + "'" : "''";
                        helpline = !!object.helpline ? "'" + app_1.Encrypt(object.helpline) + "'" : "''";
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tINSERT INTO Users (\n                Email, \n                Password,\n                StudyCode, \n                StudyId, \n                CreatedOn, \n                Status,\n                AdminID\n            )\n\t\t\tVALUES (\n\t\t        '" + app_1.Encrypt(_id + "@lamp.com") + "', \n\t\t        '',\n\t\t        " + study_code + ",\n\t\t        '" + app_1.Encrypt(_id) + "',\n\t\t        GETDATE(), \n\t\t        1,\n\t\t        " + admin_id + "\n\t\t\t);\n\t\t\tSELECT SCOPE_IDENTITY() AS id;\n\t\t")
                            // Bail early if we failed to create a User row.
                        ];
                    case 1:
                        result1 = _a.sent();
                        // Bail early if we failed to create a User row.
                        if (result1.recordset.length === 0)
                            throw new Error("404.object-not-found");
                        return [4 /*yield*/, app_1.SQL.request().query("\n            INSERT INTO UserSettings (\n                UserID, \n                AppColor,\n                SympSurvey_SlotID,\n                SympSurvey_RepeatID,\n                CognTest_SlotID,\n                CognTest_RepeatID,\n                [24By7ContactNo], \n                PersonalHelpline,\n                PrefferedSurveys,\n                PrefferedCognitions,\n                Language\n            )\n\t\t\tVALUES (\n\t\t\t    " + result1.recordset[0]["id"] + ",\n\t\t        " + theme + ",\n\t\t        1,\n\t\t        1,\n\t\t        1,\n\t\t        1,\n\t\t        " + emergency_contact + ",\n\t\t        " + helpline + ",\n\t\t        '',\n\t\t        '',\n\t\t        " + language + "\n\t\t\t);\n\t\t")
                            // Return the new row's ID.
                        ];
                    case 2:
                        result2 = _a.sent();
                        // Return the new row's ID.
                        return [2 /*return*/, { id: _id }];
                }
            });
        });
    };
    /**
     * Update a `Participant` with new fields.
     */
    ParticipantRepository._update = function (
    /**
     * The `StudyId` column of the `Users` table in the LAMP v0.1 DB.
     */
    participant_id, 
    /**
     * The patch fields of the `Participant` object.
     */
    object) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                // TODO: Objects here are immutable!
                throw new Error("503.unimplemented");
            });
        });
    };
    /**
     * Delete a `Participant`.
     */
    ParticipantRepository._delete = function (
    /**
     * The `StudyId` column of the `Users` table in the LAMP v0.1 DB.
     */
    participant_id) {
        return __awaiter(this, void 0, void 0, function () {
            var user_id, res;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        user_id = app_1.Encrypt(ParticipantRepository._unpack_id(participant_id).study_id);
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tIF EXISTS(SELECT UserID FROM Users WHERE StudyId = '" + user_id + "' AND IsDeleted != 1)\n\t\t\t\tUPDATE Users SET IsDeleted = 1 WHERE StudyId = '" + user_id + "';\n\t\t")];
                    case 1:
                        res = _a.sent();
                        if (res.rowsAffected.length === 0 || res.rowsAffected[0] === 0)
                            throw new Error("404.object-not-found");
                        return [2 /*return*/, {}];
                }
            });
        });
    };
    return ParticipantRepository;
}());
exports.ParticipantRepository = ParticipantRepository;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiUGFydGljaXBhbnRSZXBvc2l0b3J5LmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vc3JjL3JlcG9zaXRvcnkvUGFydGljaXBhbnRSZXBvc2l0b3J5LnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7O0FBQUEsOEJBQThDO0FBRTlDLHdDQUFzQztBQUN0QyxrREFBZ0Q7QUFDaEQsb0RBQWtEO0FBQ2xELDJFQUF5RTtBQUN6RSxpRUFBK0Q7QUFDL0QsK0RBQWlGO0FBRWpGO0lBQUE7SUErUUEsQ0FBQztJQTlRQzs7T0FFRztJQUNXLDhCQUFRLEdBQXRCLFVBQXVCLFVBS3RCO1FBQ0MsT0FBTyxVQUFVLENBQUMsUUFBUSxJQUFJLEVBQUUsQ0FBQTtJQUNsQyxDQUFDO0lBRUQ7O09BRUc7SUFDVyxnQ0FBVSxHQUF4QixVQUNFLEVBQVU7UUFPVixPQUFPLEVBQUUsUUFBUSxFQUFVLEVBQUUsRUFBRSxDQUFBO0lBQ2pDLENBQUM7SUFFRDs7T0FFRztJQUNpQixnQ0FBVSxHQUE5QixVQUErQixFQUFVLEVBQUUsSUFBYzs7Ozs7O3dCQUMvQyxRQUFRLEdBQUsscUJBQXFCLENBQUMsVUFBVSxDQUFDLEVBQUUsQ0FBQyxTQUF6QyxDQUF5Qzt3QkFDakQsS0FBQSxJQUFJLENBQUE7O2lDQUNMLGlDQUFlLENBQUMsQ0FBaEIsd0JBQWU7aUNBQ2YsMkNBQW9CLENBQUMsQ0FBckIsd0JBQW9COzs7NEJBRXJCLHFCQUFNLFNBQUksQ0FBQyxPQUFPLEVBQUUsQ0FBQyxLQUFLLENBQUMsMklBR29CLGFBQU8sQ0FBQyxRQUFRLENBQUMsaUJBQ3JFLENBQUMsRUFBQTs7d0JBTFEsTUFBTSxHQUFHLENBQ2IsU0FJSixDQUNHLENBQUMsU0FBUzt3QkFDWCxzQkFBTyxNQUFNLENBQUMsTUFBTSxLQUFLLENBQUM7Z0NBQ3hCLENBQUMsQ0FBQyxTQUFTO2dDQUNYLENBQUMsQ0FBQyxDQUFDLElBQUksS0FB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