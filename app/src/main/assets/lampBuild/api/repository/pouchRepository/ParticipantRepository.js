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
var app_1 = require("../../app");
var Study_1 = require("../../model/Study");
var Researcher_1 = require("../../model/Researcher");
var Participant_1 = require("../../model/Participant");
var ResearcherRepository_1 = require("../../repository/ResearcherRepository");
var StudyRepository_1 = require("../../repository/StudyRepository");
var TypeRepository_1 = require("../../repository/TypeRepository");
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiUGFydGljaXBhbnRSZXBvc2l0b3J5LmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vc3JjL3JlcG9zaXRvcnkvcG91Y2hSZXBvc2l0b3J5L1BhcnRpY2lwYW50UmVwb3NpdG9yeS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUFBLGlDQUFpRDtBQUVqRCwyQ0FBeUM7QUFDekMscURBQW1EO0FBQ25ELHVEQUFxRDtBQUNyRCw4RUFBNEU7QUFDNUUsb0VBQWtFO0FBQ2xFLGtFQUFvRjtBQUVwRjtJQUFBO0lBK1FBLENBQUM7SUE5UUM7O09BRUc7SUFDVyw4QkFBUSxHQUF0QixVQUF1QixVQUt0QjtRQUNDLE9BQU8sVUFBVSxDQUFDLFFBQVEsSUFBSSxFQUFFLENBQUE7SUFDbEMsQ0FBQztJQUVEOztPQUVHO0lBQ1csZ0NBQVUsR0FBeEIsVUFDRSxFQUFVO1FBT1YsT0FBTyxFQUFFLFFBQVEsRUFBVSxFQUFFLEVBQUUsQ0FBQTtJQUNqQyxDQUFDO0lBRUQ7O09BRUc7SUFDaUIsZ0NBQVUsR0FBOUIsVUFBK0IsRUFBVSxFQUFFLElBQWM7Ozs7Ozt3QkFDL0MsUUFBUSxHQUFLLHFCQUFxQixDQUFDLFVBQVUsQ0FBQyxFQUFFLENBQUMsU0FBekMsQ0FBeUM7d0JBQ2pELEtBQUEsSUFBSSxDQUFBOztpQ0FDTCxpQ0FBZSxDQUFDLENBQWhCLHdCQUFlO2lDQUNmLDJDQUFvQixDQUFDLENBQXJCLHdCQUFvQjs7OzRCQUVyQixxQkFBTSxTQUFJLENBQUMsT0FBTyxFQUFFLENBQUMsS0FBSyxDQUFDLDJJQUdvQixhQUFPLENBQUMsUUFBUSxDQUFDLGlCQUNyRSxDQUFDLEVBQUE7O3dCQUxRLE1BQU0sR0FBRyxDQUNiLFNBSUosQ0FDRyxDQUFDLFNBQVM7d0JBQ1gsc0JBQU8sTUFBTSxDQUFDLE1BQU0sS0FBSyxDQUFDO2dDQUN4QixDQUFDLENBQUMsU0FBUztnQ0FDWCxDQUFD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