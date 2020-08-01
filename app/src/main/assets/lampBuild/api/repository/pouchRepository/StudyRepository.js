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
var ResearcherRepository_1 = require("../../repository/ResearcherRepository");
var ActivityRepository_1 = require("../../repository/ActivityRepository");
var TypeRepository_1 = require("../../repository/TypeRepository");
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiU3R1ZHlSZXBvc2l0b3J5LmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vc3JjL3JlcG9zaXRvcnkvcG91Y2hSZXBvc2l0b3J5L1N0dWR5UmVwb3NpdG9yeS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUFBLGlDQUFpRDtBQUlqRCwyQ0FBeUM7QUFDekMscURBQW1EO0FBQ25ELDhFQUE0RTtBQUU1RSwwRUFBd0U7QUFDeEUsa0VBQW9GO0FBRXBGO0lBQUE7SUErTEEsQ0FBQztJQTlMQzs7T0FFRztJQUNXLHdCQUFRLEdBQXRCLFVBQXVCLFVBS3RCO1FBQ0MsT0FBTyxnQ0FBZSxDQUFDLENBQU8sYUFBTSxDQUFDLElBQUksRUFBRSxVQUFVLENBQUMsUUFBUSxJQUFJLENBQUMsQ0FBQyxDQUFDLENBQUE7SUFDdkUsQ0FBQztJQUVEOztPQUVHO0lBQ1csMEJBQVUsR0FBeEIsVUFDRSxFQUFVO1FBT1YsSUFBTSxVQUFVLEdBQUcsa0NBQWlCLENBQUMsRUFBRSxDQUFDLENBQUE7UUFDeEMsSUFBSSxVQUFVLENBQUMsQ0FBQyxDQUFDLEtBQVcsYUFBTSxDQUFDLElBQUk7WUFBRSxNQUFNLElBQUksS0FBSyxDQUFDLHdCQUF3QixDQUFDLENBQUE7UUFDbEYsSUFBTSxNQUFNLEdBQUcsVUFBVSxDQUFDLEtBQUssQ0FBQyxDQUFDLENBQUMsQ0FBQyxHQUFHLENBQUMsVUFBQyxDQUFDLHdCQUFLLE1BQU0sQ0FBQyxLQUFLLENBQUMsQ0FBQyxDQUFDLHVDQUFJLENBQUMsSUFBQSxDQUFDLENBQUE7UUFDbkUsT0FBTztZQUNMLFFBQVEsRUFBRSxNQUFNLENBQUMsQ0FBQyxDQUFDO1NBQ3BCLENBQUE7SUFDSCxDQUFDO0lBRUQ7O09BRUc7SUFDaUIsMEJBQVUsR0FBOUIsVUFBK0IsRUFBVSxFQUFFLElBQWM7Ozs7Z0JBQy9DLFFBQVEsR0FBSyxlQUFlLENBQUMsVUFBVSxDQUFD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