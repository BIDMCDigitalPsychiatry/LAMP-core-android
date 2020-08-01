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
var ResearcherRepository_1 = require("../repository/ResearcherRepository");
var ActivityRepository_1 = require("../repository/ActivityRepository");
var TypeRepository_1 = require("../repository/TypeRepository");
var StudyRepository = /** @class */ (function () {
    function StudyRepository() {
    }
    /**
     *
     */
    StudyRepository._pack_id = function (components) {
        return TypeRepository_1.Identifier_pack([Study_1.Study.name, components.admin_id || 0]);
    };
    /**
     *
     */
    StudyRepository._unpack_id = function (id) {
        var components = TypeRepository_1.Identifier_unpack(id);
        if (components[0] !== Study_1.Study.name)
            throw new Error("400.invalid-identifier");
        var result = components.slice(1).map(function (x) { var _a; return _a = Number.parse(x), (_a !== null && _a !== void 0 ? _a : 0); });
        return {
            admin_id: result[0],
        };
    };
    /**
     *
     */
    StudyRepository._parent_id = function (id, type) {
        return __awaiter(this, void 0, void 0, function () {
            var admin_id;
            return __generator(this, function (_a) {
                admin_id = StudyRepository._unpack_id(id).admin_id;
                switch (type) {
                    case ResearcherRepository_1.ResearcherRepository:
                        return [2 /*return*/, ResearcherRepository_1.ResearcherRepository._pack_id({ admin_id: admin_id })];
                    default:
                        throw new Error("400.invalid-identifier");
                }
                return [2 /*return*/];
            });
        });
    };
    /**
     * Get a set of `Study`s matching the criteria parameters.
     */
    StudyRepository._select = function (
    /**
     *
     */
    id) {
        return __awaiter(this, void 0, void 0, function () {
            var admin_id, result;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!!id && TypeRepository_1.Identifier_unpack(id)[0] === Researcher_1.Researcher.name)
                            admin_id = ResearcherRepository_1.ResearcherRepository._unpack_id(id).admin_id;
                        else if (!!id && TypeRepository_1.Identifier_unpack(id)[0] === Study_1.Study.name)
                            admin_id = StudyRepository._unpack_id(id).admin_id;
                        else if (!!id)
                            throw new Error("400.invalid-identifier");
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tSELECT \n                Admin.AdminID AS id, \n                ('Default Study') AS name, \n                (\n                    SELECT \n                        StudyId AS id\n                    FROM Users\n                    WHERE IsDeleted = 0 \n                        AND Users.AdminID = Admin.AdminID\n                    FOR JSON PATH, INCLUDE_NULL_VALUES\n                ) AS participants,\n                (\n                    SELECT \n                        SurveyID AS id\n                    FROM Survey\n                    WHERE IsDeleted = 0 \n                        AND Survey.AdminID = Admin.AdminID\n                    FOR JSON PATH, INCLUDE_NULL_VALUES\n                ) AS surveys,\n                (\n                    SELECT \n                        AdminCTestSettingID AS id\n                    FROM Admin_CTestSettings\n                    WHERE Status = 1\n                    FOR JSON PATH, INCLUDE_NULL_VALUES\n                ) AS ctests,\n                (\n                    SELECT \n                        AdminBatchSchID AS id\n                    FROM Admin_BatchSchedule\n                    WHERE IsDeleted = 0\n                    FOR JSON PATH, INCLUDE_NULL_VALUES\n                ) AS groups\n            FROM Admin\n            LEFT JOIN Admin_Settings\n                ON Admin_Settings.AdminID = Admin.AdminID\n            WHERE \n            \tIsDeleted = 0 \n            \t" + (!!admin_id ? "AND Admin.AdminID = '" + admin_id + "'" : "") + "\n            FOR JSON PATH, INCLUDE_NULL_VALUES;\n\t\t")];
                    case 1:
                        result = _a.sent();
                        if (result.recordset.length === 0)
                            return [2 /*return*/, []];
                        return [2 /*return*/, result.recordset[0].map(function (raw) {
                                var obj = new Study_1.Study();
                                obj.id = StudyRepository._pack_id({ admin_id: raw.id });
                                obj.name = raw.name;
                                obj.participants = (raw.participants || []).map(function (x) {
                                    return app_1.Decrypt(x.id);
                                });
                                obj.activities = [].concat((raw.surveys || []).map(function (x) {
                                    return ActivityRepository_1.ActivityRepository._pack_id({
                                        ctest_id: 0,
                                        survey_id: x.id,
                                        group_id: 0,
                                    });
                                }), (raw.ctests || []).map(function (x) {
                                    return ActivityRepository_1.ActivityRepository._pack_id({
                                        ctest_id: x.id,
                                        survey_id: 0,
                                        group_id: 0,
                                    });
                                }), (raw.groups || []).map(function (x) {
                                    return ActivityRepository_1.ActivityRepository._pack_id({
                                        ctest_id: 0,
                                        survey_id: 0,
                                        group_id: x.id,
                                    });
                                }));
                                return obj;
                            })];
                }
            });
        });
    };
    /**
     * Create a `Study` with a new object.
     */
    StudyRepository._insert = function (
    /**
     * The `AdminID` column of the `Admin` table in the LAMP v0.1 DB.
     */
    researcher_id, 
    /**
     * The new object.
     */
    object) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                // TODO: Studies do not exist! They cannot be modified!
                throw new Error("503.unimplemented");
            });
        });
    };
    /**
     * Update a `Study` with new fields.
     */
    StudyRepository._update = function (
    /**
     * The `AdminID` column of the `Admin` table in the LAMP v0.1 DB.
     */
    study_id, 
    /**
     * The replacement object or specific fields within.
     */
    object) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                // TODO: Studies do not exist! They cannot be modified!
                throw new Error("503.unimplemented");
            });
        });
    };
    /**
     * Delete a `Study` row.
     */
    StudyRepository._delete = function (
    /**
     * The `AdminID` column of the `Admin` table in the LAMP v0.1 DB.
     */
    study_id) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                // TODO: Studies do not exist! They cannot be modified!
                throw new Error("503.unimplemented");
            });
        });
    };
    return StudyRepository;
}());
exports.StudyRepository = StudyRepository;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiU3R1ZHlSZXBvc2l0b3J5LmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vc3JjL3JlcG9zaXRvcnkvU3R1ZHlSZXBvc2l0b3J5LnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7O0FBQUEsOEJBQThDO0FBSTlDLHdDQUFzQztBQUN0QyxrREFBZ0Q7QUFDaEQsMkVBQXlFO0FBRXpFLHVFQUFxRTtBQUNyRSwrREFBaUY7QUFFakY7SUFBQTtJQStMQSxDQUFDO0lBOUxDOztPQUVHO0lBQ1csd0JBQVEsR0FBdEIsVUFBdUIsVUFLdEI7UUFDQyxPQUFPLGdDQUFlLENBQUMsQ0FBTyxhQUFNLENBQUMsSUFBSSxFQUFFLFVBQVUsQ0FBQyxRQUFRLElBQUksQ0FBQyxDQUFDLENBQUMsQ0FBQTtJQUN2RSxDQUFDO0lBRUQ7O09BRUc7SUFDVywwQkFBVSxHQUF4QixVQUNFLEVBQVU7UUFPVixJQUFNLFVBQVUsR0FBRyxrQ0FBaUIsQ0FBQyxFQUFFLENBQUMsQ0FBQTtRQUN4QyxJQUFJLFVBQVUsQ0FBQyxDQUFDLENBQUMsS0FBVyxhQUFNLENBQUMsSUFBSTtZQUFFLE1BQU0sSUFBSSxLQUFLLENBQUMsd0JBQXdCLENBQUMsQ0FBQTtRQUNsRixJQUFNLE1BQU0sR0FBRyxVQUFVLENBQUMsS0FBSyxDQUFDLENBQUMsQ0FBQyxDQUFDLEdBQUcsQ0FBQyxVQUFDLENBQUMsd0JBQUssTUFBTSxDQUFDLEtBQUssQ0FBQyxDQUFDLENBQUMsdUNBQUksQ0FBQyxJQUFBLENBQUMsQ0FBQTtRQUNuRSxPQUFPO1lBQ0wsUUFBUSxFQUFFLE1BQU0sQ0FBQyxDQUFDLENBQUM7U0FDcEIsQ0FBQTtJQUNILENBQUM7SUFFRDs7T0FFRztJQUNpQiwwQkFBVSxHQUE5QixVQUErQixFQUFVLEVBQUUsSUFBYzs7OztnQkFDL0MsUUFBUSxHQUFLLGVBQWUsQ0FBQyxVQUFVLENBQUMsRUFBRSxDQUFDLFNBQW5DLENB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