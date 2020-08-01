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
var app_1 = require("../../app");
var Activity_1 = require("../../model/Activity");
var Study_1 = require("../../model/Study");
var Researcher_1 = require("../../model/Researcher");
var ResearcherRepository_1 = require("../../repository/ResearcherRepository");
var StudyRepository_1 = require("../../repository/StudyRepository");
var TypeRepository_1 = require("../../repository/TypeRepository");
var TypeRepository_2 = require("../../repository/TypeRepository");
var migrate_1 = require("./migrate");
var ActivityRepository = /** @class */ (function () {
    function ActivityRepository() {
    }
    /**
     *
     */
    ActivityRepository._pack_id = function (components) {
        return TypeRepository_2.Identifier_pack([
            Activity_1.Activity.name,
            components.ctest_id || 0,
            components.survey_id || 0,
            components.group_id || 0,
        ]);
    };
    /**
     *
     */
    ActivityRepository._unpack_id = function (id) {
        var components = TypeRepository_2.Identifier_unpack(id);
        if (components[0] !== Activity_1.Activity.name)
            throw new Error("400.invalid-identifier");
        var result = components.slice(1).map(function (x) { var _a; return _a = Number.parse(x), (_a !== null && _a !== void 0 ? _a : 0); });
        return {
            ctest_id: result[0],
            survey_id: result[1],
            group_id: result[2],
        };
    };
    /**
     *
     */
    ActivityRepository._parent_id = function (id, type) {
        return __awaiter(this, void 0, void 0, function () {
            var _a, ctest_id, survey_id, group_id, _b, result, result, result;
            return __generator(this, function (_c) {
                switch (_c.label) {
                    case 0:
                        _a = ActivityRepository._unpack_id(id), ctest_id = _a.ctest_id, survey_id = _a.survey_id, group_id = _a.group_id;
                        _b = type;
                        switch (_b) {
                            case StudyRepository_1.StudyRepository: return [3 /*break*/, 1];
                            case ResearcherRepository_1.ResearcherRepository: return [3 /*break*/, 1];
                        }
                        return [3 /*break*/, 8];
                    case 1:
                        if (!(survey_id > 0) /* survey */) return [3 /*break*/, 3]; /* survey */
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\t\t\t\tSELECT AdminID AS value\n\t\t\t\t\t\tFROM Survey\n\t\t\t\t\t\tWHERE IsDeleted = 0 AND SurveyID = '" + survey_id + "'\n\t\t\t\t\t;")];
                    case 2:
                        result = (_c.sent()).recordset;
                        return [2 /*return*/, result.length === 0
                                ? undefined
                                : (type === ResearcherRepository_1.ResearcherRepository ? ResearcherRepository_1.ResearcherRepository : StudyRepository_1.StudyRepository)._pack_id({
                                    admin_id: result[0].value,
                                })];
                    case 3:
                        if (!(ctest_id > 0) /* ctest */) return [3 /*break*/, 5]; /* ctest */
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\t\t\t\tSELECT AdminID AS value\n\t\t\t\t\t\tFROM Admin_CTestSettings\n\t\t\t\t\t\tWHERE Status = 1 AND AdminCTestSettingID = '" + ctest_id + "'\n\t\t\t\t\t;")];
                    case 4:
                        result = (_c.sent()).recordset;
                        return [2 /*return*/, result.length === 0
                                ? undefined
                                : (type === ResearcherRepository_1.ResearcherRepository ? ResearcherRepository_1.ResearcherRepository : StudyRepository_1.StudyRepository)._pack_id({
                                    admin_id: result[0].value,
                                })];
                    case 5:
                        if (!(group_id > 0) /* group */) return [3 /*break*/, 7]; /* group */
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\t\t\t\tSELECT AdminID AS value\n\t\t\t\t\t\tFROM Admin_BatchSchedule\n\t\t\t\t\t\tWHERE IsDeleted = 0 AND AdminBatchSchID = '" + ctest_id + "'\n\t\t\t\t\t;")];
                    case 6:
                        result = (_c.sent()).recordset;
                        return [2 /*return*/, result.length === 0
                                ? undefined
                                : (type === ResearcherRepository_1.ResearcherRepository ? ResearcherRepository_1.ResearcherRepository : StudyRepository_1.StudyRepository)._pack_id({
                                    admin_id: result[0].value,
                                })];
                    case 7: return [2 /*return*/, undefined];
                    case 8: throw new Error("400.invalid-identifier");
                }
            });
        });
    };
    // FIXME: Use AdminCTestSettings for CTest ID
    /**
     * Get a set of `Activity`s matching the criteria parameters.
     */
    ActivityRepository._select = function (
    /**
     *
     */
    id) {
        return __awaiter(this, void 0, void 0, function () {
            var ctest_id, survey_id, group_id, admin_id, c, _a, _b, resultBatch, resultBatchCTestSettings, resultBatchSurveySettings, resultBatchSchedule, resultSurvey, resultSurveyQuestions, resultSurveySchedule, resultTest, resultTestJewelsSettings, resultTestSchedule;
            return __generator(this, function (_c) {
                switch (_c.label) {
                    case 0:
                        if (!(!!id && TypeRepository_2.Identifier_unpack(id)[0] === Researcher_1.Researcher.name)) return [3 /*break*/, 1];
                        admin_id = ResearcherRepository_1.ResearcherRepository._unpack_id(id).admin_id;
                        return [3 /*break*/, 6];
                    case 1:
                        if (!(!!id && TypeRepository_2.Identifier_unpack(id)[0] === Study_1.Study.name)) return [3 /*break*/, 2];
                        admin_id = StudyRepository_1.StudyRepository._unpack_id(id).admin_id;
                        return [3 /*break*/, 6];
                    case 2:
                        if (!(!!id && TypeRepository_2.Identifier_unpack(id)[0] === Activity_1.Activity.name)) return [3 /*break*/, 3];
                        c = ActivityRepository._unpack_id(id);
                        ctest_id = c.ctest_id;
                        survey_id = c.survey_id;
                        group_id = c.group_id;
                        return [3 /*break*/, 6];
                    case 3:
                        if (!(!!id && TypeRepository_2.Identifier_unpack(id).length === 0) /* Participant */) return [3 /*break*/, 5]; /* Participant */
                        _b = (_a = ResearcherRepository_1.ResearcherRepository)._unpack_id;
                        return [4 /*yield*/, TypeRepository_1.TypeRepository._parent(id)];
                    case 4:
                        admin_id = _b.apply(_a, [(_c.sent())["Researcher"]]).admin_id;
                        return [3 /*break*/, 6];
                    case 5:
                        if (!!id)
                            throw new Error("400.invalid-identifier");
                        _c.label = 6;
                    case 6: return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tSELECT \n\t\t\t\tAdminBatchSchID AS id, \n\t\t\t\tAdminID AS aid,\n\t\t\t\tBatchName AS name, \n\t\t\t\t('batch') AS type\n\t\t\tFROM Admin_BatchSchedule\n      WHERE IsDeleted = 0 \n        " + (((ctest_id !== null && ctest_id !== void 0 ? ctest_id : 0)) > 0 || ((survey_id !== null && survey_id !== void 0 ? survey_id : 0)) > 0 ? "AND 1=0" : "") + "\n\t\t\t\t" + ((group_id !== null && group_id !== void 0 ? group_id : 0 > 0) ? "AND AdminBatchSchID = '" + group_id + "'" : "") + "\n\t\t\t\t" + ((admin_id !== null && admin_id !== void 0 ? admin_id : 0 > 0) ? "AND AdminID = '" + admin_id + "'" : "") + "\n\t\t;")];
                    case 7:
                        resultBatch = (_c.sent()).recordset;
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tSELECT \n\t\t\t\tAdmin_BatchScheduleCTest.AdminBatchSchID AS id, \n\t\t\t\tAdmin_CTestSettings.AdminCTestSettingID AS ctest_id,\n\t\t\t\t[Order] AS [order]\n      FROM Admin_BatchScheduleCTest\n      JOIN Admin_BatchSchedule\n        ON Admin_BatchSchedule.AdminBatchSchID = Admin_BatchScheduleCTest.AdminBatchSchID\n\t\t\tJOIN Admin_CTestSettings\n        ON Admin_CTestSettings.CTestID = Admin_BatchScheduleCTest.CTestID \n        AND Admin_CTestSettings.AdminID = Admin_BatchSchedule.AdminID\n\t\t\tWHERE Admin_BatchScheduleCTest.AdminBatchSchID IN (" + (resultBatch.length === 0 ? "NULL" : resultBatch.map(function (x) { return x.id; }).join(",")) + ")\n\t\t;")];
                    case 8:
                        resultBatchCTestSettings = (_c.sent()).recordset;
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tSELECT \n\t\t\t\tAdminBatchSchID AS id, \n\t\t\t\tSurveyID AS survey_id, \n\t\t\t\t[Order] AS [order]\n\t\t\tFROM Admin_BatchScheduleSurvey\n\t\t\tWHERE AdminBatchSchID IN (" + (resultBatch.length === 0 ? "NULL" : resultBatch.map(function (x) { return x.id; }).join(",")) + ")\n\t\t;")];
                    case 9:
                        resultBatchSurveySettings = (_c.sent()).recordset;
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tSELECT\n\t\t\t\tAdminBatchSchID AS id, \n\t\t\t\tScheduleDate as start_date,\n\t\t\t\tTime as time,\n\t\t\t\tCHOOSE(RepeatID, \n\t\t\t\t\t'hourly', 'every3h', 'every6h', 'every12h', 'daily', \n\t\t\t\t\t'biweekly', 'triweekly', 'weekly', 'bimonthly', \n\t\t\t\t\t'monthly', 'custom', 'none'\n\t\t\t\t) AS repeat_interval, \n\t\t\t\t(\n\t\t\t\t\tSELECT \n\t\t\t\t\t\tTime AS t\n\t\t\t\t\tFROM Admin_BatchScheduleCustomTime\n\t\t\t\t\tWHERE Admin_BatchScheduleCustomTime.AdminBatchSchId = Admin_BatchSchedule.AdminBatchSchId\n\t\t\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES\n\t\t\t\t) AS custom_time\n\t\t\tFROM Admin_BatchSchedule\n\t\t\tWHERE IsDeleted = 0 \n\t\t\t\tAND AdminBatchSchID IN (" + (resultBatch.length === 0 ? "NULL" : resultBatch.map(function (x) { return x.id; }).join(",")) + ")\n\t\t;")];
                    case 10:
                        resultBatchSchedule = (_c.sent()).recordset;
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tSELECT \n\t\t\t\tSurveyID AS id, \n\t\t\t\tAdminID AS aid,\n\t\t\t\tSurveyName AS name, \n\t\t\t\t('survey') AS type\n\t\t\tFROM Survey\n\t\t\tWHERE IsDeleted = 0 \n        " + (((ctest_id !== null && ctest_id !== void 0 ? ctest_id : 0)) > 0 || ((group_id !== null && group_id !== void 0 ? group_id : 0)) > 0 ? "AND 1=0" : "") + "\n\t\t\t\t" + ((survey_id !== null && survey_id !== void 0 ? survey_id : 0 > 0) ? "AND SurveyID = '" + survey_id + "'" : "") + "\n\t\t\t\t" + ((admin_id !== null && admin_id !== void 0 ? admin_id : 0 > 0) ? "AND AdminID = '" + admin_id + "'" : "") + "\n\t\t;")];
                    case 11:
                        resultSurvey = (_c.sent()).recordset;
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tSELECT \n\t\t\t\tSurveyID AS id,\n\t\t\t\tQuestionText AS text, \n\t\t\t\tCHOOSE(AnswerType, \n\t\t\t\t\t'likert', 'list', 'boolean', 'clock', 'years', 'months', 'days', 'text'\n\t\t\t\t) AS type, \n\t\t\t\t(\n\t\t\t\t\tSELECT \n\t\t\t\t\t\tOptionText AS opt\n\t\t\t\t\tFROM SurveyQuestionOptions\n\t\t\t\t\tWHERE SurveyQuestionOptions.QuestionID = SurveyQuestions.QuestionID\n\t\t\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES\n\t\t\t\t) AS options\n\t\t\t\tFROM SurveyQuestions\n\t\t\t\tWHERE IsDeleted = 0 \n\t\t\t\t\tAND SurveyID IN (" + (resultSurvey.length === 0 ? "NULL" : resultSurvey.map(function (x) { return x.id; }).join(",")) + ")\n\t\t;")];
                    case 12:
                        resultSurveyQuestions = (_c.sent()).recordset;
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tSELECT\n\t\t\t\tSurveyID AS id, \n\t\t\t\tScheduleDate as start_date,\n\t\t\t\tTime as time,\n\t\t\t\tCHOOSE(RepeatID, \n\t\t\t\t\t'hourly', 'every3h', 'every6h', 'every12h', 'daily', \n\t\t\t\t\t'biweekly', 'triweekly', 'weekly', 'bimonthly', \n\t\t\t\t\t'monthly', 'custom', 'none'\n\t\t\t\t) AS repeat_interval, \n\t\t\t\t(\n\t\t\t\t\tSELECT \n\t\t\t\t\t\tTime AS t\n\t\t\t\t\tFROM Admin_SurveyScheduleCustomTime\n\t\t\t\t\tWHERE Admin_SurveyScheduleCustomTime.AdminSurveySchId = Admin_SurveySchedule.AdminSurveySchId\n\t\t\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES\n\t\t\t\t) AS custom_time\n\t\t\tFROM Admin_SurveySchedule\n\t\t\tWHERE IsDeleted = 0 \n\t\t\t\tAND Admin_SurveySchedule.SurveyID IN (" + (resultSurvey.length === 0 ? "NULL" : resultSurvey.map(function (x) { return x.id; }).join(",")) + ")\n\t\t;")];
                    case 13:
                        resultSurveySchedule = (_c.sent()).recordset;
                        return [4 /*yield*/, app_1.SQL.request().query("\n      SELECT \n        AdminCTestSettingID AS id,\n\t\t\t\tAdminID AS aid,\n        ('ctest') AS type,\n        CTestID AS ctest_id\n\t\t\tFROM Admin_CTestSettings\n      WHERE Status IN (1, NULL)\n        AND CTestID NOT IN (4, 13)\n        " + (((survey_id !== null && survey_id !== void 0 ? survey_id : 0)) > 0 || ((group_id !== null && group_id !== void 0 ? group_id : 0)) > 0 ? "AND 1=0" : "") + "\n\t\t\t\t" + ((ctest_id !== null && ctest_id !== void 0 ? ctest_id : 0 > 0) ? "AND AdminCTestSettingID = '" + ctest_id + "'" : "") + "\n\t\t\t\t" + ((admin_id !== null && admin_id !== void 0 ? admin_id : 0 > 0) ? "AND AdminID = '" + admin_id + "'" : "") + "\n\t\t;")];
                    case 14:
                        resultTest = (_c.sent()).recordset;
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tSELECT \n\t\t\t\t('a') AS type,\n\t\t\t\tNoOfSeconds_Beg AS beginner_seconds,\n\t\t\t\tNoOfSeconds_Int AS intermediate_seconds,\n\t\t\t\tNoOfSeconds_Adv AS advanced_seconds,\n\t\t\t\tNoOfSeconds_Exp AS expert_seconds,\n\t\t\t\tNoOfDiamonds AS diamond_count,\n\t\t\t\tNoOfShapes AS shape_count,\n\t\t\t\tNoOfBonusPoints AS bonus_point_count,\n\t\t\t\tX_NoOfChangesInLevel AS x_changes_in_level_count,\n\t\t\t\tX_NoOfDiamonds AS x_diamond_count,\n\t\t\t\tY_NoOfChangesInLevel AS y_changes_in_level_count,\n\t\t\t\tY_NoOfShapes AS y_shape_count\n\t\t\tFROM Admin_JewelsTrailsASettings\n\t\t\tWHERE Admin_JewelsTrailsASettings.AdminID IN (" + (resultTest.length === 0 ? "NULL" : resultTest.map(function (x) { return x.aid; }).join(",")) + ")\n\t\t\tUNION ALL\n\t\t\tSELECT \n\t\t\t\t('b') AS type,\n\t\t\t\tNoOfSeconds_Beg AS beginner_seconds,\n\t\t\t\tNoOfSeconds_Int AS intermediate_seconds,\n\t\t\t\tNoOfSeconds_Adv AS advanced_seconds,\n\t\t\t\tNoOfSeconds_Exp AS expert_seconds,\n\t\t\t\tNoOfDiamonds AS diamond_count,\n\t\t\t\tNoOfShapes AS shape_count,\n\t\t\t\tNoOfBonusPoints AS bonus_point_count,\n\t\t\t\tX_NoOfChangesInLevel AS x_changes_in_level_count,\n\t\t\t\tX_NoOfDiamonds AS x_diamond_count,\n\t\t\t\tY_NoOfChangesInLevel AS y_changes_in_level_count,\n\t\t\t\tY_NoOfShapes AS y_shape_count\n\t\t\tFROM Admin_JewelsTrailsBSettings\n\t\t\tWHERE Admin_JewelsTrailsBSettings.AdminID IN (" + (resultTest.length === 0 ? "NULL" : resultTest.map(function (x) { return x.aid; }).join(",")) + ")\n\t\t;")];
                    case 15:
                        resultTestJewelsSettings = (_c.sent()).recordset;
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tSELECT\n\t\t\t\tAdminCTestSettingID as setting_id,\n\t\t\t\tScheduleDate as start_date,\n\t\t\t\tTime as time,\n\t\t\t\tCHOOSE(RepeatID, \n\t\t\t\t\t'hourly', 'every3h', 'every6h', 'every12h', 'daily', \n\t\t\t\t\t'biweekly', 'triweekly', 'weekly', 'bimonthly', \n\t\t\t\t\t'monthly', 'custom', 'none'\n\t\t\t\t) AS repeat_interval, \n\t\t\t\t(\n\t\t\t\t\tSELECT \n\t\t\t\t\t\tTime AS t\n\t\t\t\t\tFROM Admin_CTestScheduleCustomTime\n\t\t\t\t\tWHERE Admin_CTestScheduleCustomTime.AdminCTestSchId = Admin_CTestSchedule.AdminCTestSchId\n\t\t\t\t\tFOR JSON PATH, INCLUDE_NULL_VALUES\n\t\t\t\t) AS custom_time\n      FROM Admin_CTestSchedule\n      JOIN Admin_CTestSettings \n        ON Admin_CTestSettings.AdminID = Admin_CTestSchedule.AdminID \n        AND Admin_CTestSettings.CTestID = Admin_CTestSchedule.CTestID\n\t\t\tWHERE IsDeleted = 0 \n\t\t\t\tAND AdminCTestSettingID IN (" + (resultTest.length === 0 ? "NULL" : resultTest.map(function (x) { return x.id; })) + ")\n\t\t;")];
                    case 16:
                        resultTestSchedule = (_c.sent()).recordset;
                        // FIXME: Shouldn't return deleted surveys/ctests in group settings.
                        return [2 /*return*/, __spread(resultBatch, resultSurvey, resultTest).map(function (raw) {
                                var obj = new Activity_1.Activity();
                                if (raw.type === "batch") {
                                    obj.id = ActivityRepository._pack_id({
                                        group_id: raw.id,
                                    });
                                    obj.spec = "lamp.group";
                                    obj.name = raw.name;
                                    obj.settings = __spread(resultBatchSurveySettings.filter(function (x) { return x.id === raw.id; }), resultBatchCTestSettings.filter(function (x) { return x.id === raw.id; })).sort(function (x, y) { return x.order - y.order; })
                                        .map(function (x) {
                                        return ActivityRepository._pack_id({
                                            ctest_id: !x.ctest_id ? undefined : x.ctest_id,
                                            survey_id: !x.survey_id ? undefined : x.survey_id,
                                        });
                                    });
                                    obj.schedule = resultBatchSchedule
                                        .filter(function (x) { return x.id === raw.id; })
                                        .map(function (x) { return (__assign(__assign({}, x), { id: undefined, custom_time: !x.custom_time ? null : JSON.parse(x.custom_time).map(function (y) { return y.t; }) })); });
                                }
                                else if (raw.type === "survey") {
                                    obj.id = ActivityRepository._pack_id({
                                        survey_id: raw.id,
                                    });
                                    obj.spec = "lamp.survey";
                                    obj.name = raw.name;
                                    obj.settings = resultSurveyQuestions
                                        .filter(function (x) { return x.id === raw.id; })
                                        .map(function (x) { return (__assign(__assign({}, x), { id: undefined, options: !x.options ? null : JSON.parse(x.options).map(function (y) { return y.opt; }) })); });
                                    obj.schedule = resultSurveySchedule
                                        .filter(function (x) { return x.id === raw.id; })
                                        .map(function (x) { return (__assign(__assign({}, x), { id: undefined, custom_time: !x.custom_time ? null : JSON.parse(x.custom_time).map(function (y) { return y.t; }) })); });
                                }
                                else if (raw.type === "ctest") {
                                    obj.id = ActivityRepository._pack_id({
                                        ctest_id: raw.id,
                                    });
                                    // FIXME: account for Forward/Backward variants that are not mapped!
                                    var specEntry = migrate_1.ActivityIndex.find(function (x) { return x.LegacyCTestID === Number.parse(raw.ctest_id); });
                                    obj.spec = specEntry.Name;
                                    obj.name = spec_map[specEntry.Name];
                                    if (specEntry.Name === "lamp.jewels_a") {
                                        obj.settings = resultTestJewelsSettings
                                            .filter(function (x) { return x.type === "a"; })
                                            .map(function (x) { return (__assign(__assign({}, x), { type: undefined })); })[0];
                                    }
                                    else if (specEntry.Name === "lamp.jewels_b") {
                                        obj.settings = resultTestJewelsSettings
                                            .filter(function (x) { return x.type === "b"; })
                                            .map(function (x) { return (__assign(__assign({}, x), { type: undefined })); })[0];
                                    }
                                    else
                                        obj.settings = {};
                                    obj.schedule = resultTestSchedule
                                        .filter(function (x) { return x.setting_id == raw.id; })
                                        .map(function (x) { return (__assign(__assign({}, x), { setting_id: undefined, custom_time: !x.custom_time ? null : JSON.parse(x.custom_time).map(function (y) { return y.t; }) })); });
                                }
                                return obj;
                            })];
                }
            });
        });
    };
    /**
     * Create an `Activity` with a new object.
     */
    ActivityRepository._insert = function (
    /**
     * The parent Study's ID.
     */
    study_id, 
    /**
     * The new object.
     */
    object) {
        var _a, _b, _c, _d, _e, _f;
        return __awaiter(this, void 0, void 0, function () {
            var admin_id, transaction, result1_1, batch_id_1, ctime, result2, items, _ctest_select_1, result3, result4, result1, survey_id_1, result2_1, opts, result21, result3_1, ctime, result4, ctest_id_1, result, _actual_setting_id, isA, result2, result3_2, ctime, result4, e_1;
            return __generator(this, function (_g) {
                switch (_g.label) {
                    case 0:
                        admin_id = StudyRepository_1.StudyRepository._unpack_id(study_id).admin_id;
                        transaction = app_1.SQL.transaction();
                        return [4 /*yield*/, transaction.begin()];
                    case 1:
                        _g.sent();
                        _g.label = 2;
                    case 2:
                        _g.trys.push([2, 29, , 31]);
                        if (!(object.spec === "lamp.group") /* group */) return [3 /*break*/, 11]; /* group */
                        if (!Array.isArray(object.schedule) || object.schedule.length !== 1)
                            throw new Error("400.duration-interval-not-specified");
                        if (!Array.isArray(object.settings) || object.settings.length === 0)
                            throw new Error("400.settings-not-specified");
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\tINSERT INTO Admin_BatchSchedule (\n\t\t\t\t\t\tAdminID,\n\t\t\t\t\t\tBatchName,\n\t\t\t\t\t\tScheduleDate,\n\t\t\t\t\t\tTime,\n\t\t\t\t\t\tRepeatID\n\t\t\t\t\t) \n\t\t\t\t\tOUTPUT INSERTED.AdminBatchSchId\n\t\t\t\t\tVALUES " + object.schedule
                                .map(function (sched) { return "(\n\t\t\t\t\t\t" + admin_id + ", \n\t\t\t\t\t\t'" + (object.name ? _escapeMSSQL(object.name) : "new_group") + "',\n\t\t\t\t\t\t'" + sched.start_date + "',\n\t\t\t\t\t\t'" + sched.time + "',\n\t\t\t\t\t\t" + ([
                                "hourly",
                                "every3h",
                                "every6h",
                                "every12h",
                                "daily",
                                "biweekly",
                                "triweekly",
                                "weekly",
                                "bimonthly",
                                "monthly",
                                "custom",
                                "none",
                            ].indexOf(sched.repeat_interval) + 1) + "\n\t\t\t\t\t)"; })
                                .join(", ") + "\n\t\t\t\t;")];
                    case 3:
                        result1_1 = _g.sent();
                        if (result1_1.rowsAffected[0] !== object.schedule.length)
                            throw new Error("400.create-failed-due-to-malformed-parameters-schedule");
                        batch_id_1 = (_a = Number.parse(result1_1.recordset[0]["AdminBatchSchId"]), (_a !== null && _a !== void 0 ? _a : -1));
                        ctime = [].concat.apply([], __spread(object.schedule
                            .map(function (x, idx) { return [x.custom_time, idx]; })
                            .filter(function (x) { return !!x[0]; })
                            .map(function (x) { return x[0].map(function (y) { return [y, x[1]]; }); })));
                        if (!(ctime.length > 0)) return [3 /*break*/, 5];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\tINSERT INTO Admin_BatchScheduleCustomTime (\n\t\t\t\t\t\t\tAdminBatchSchId,\n\t\t\t\t\t\t\tTime\n\t\t\t\t\t\t)\n\t\t\t\t\t\tVALUES " + ctime
                                .map(function (x) { return "(\n\t\t\t\t\t\t\t" + result1_1.recordset[x[1]]["AdminBatchSchId"] + ",\n\t\t\t\t\t\t\t'" + x[0] + "'\n\t\t\t\t\t\t)"; })
                                .join(", ") + "\n\t\t\t\t\t;")];
                    case 4:
                        result2 = _g.sent();
                        if (result2.rowsAffected[0] === 0)
                            throw new Error("400.create-failed-due-to-malformed-parameters-timing");
                        _g.label = 5;
                    case 5:
                        items = object.settings.map(function (x, idx) { return (__assign(__assign({}, ActivityRepository._unpack_id(x)), { idx: idx })); });
                        if (items.filter(function (x) { return x.group_id > 0; }).length > 0)
                            throw new Error("400.nested-objects-unsupported");
                        _ctest_select_1 = function (x_ctest_id) {
                            return "SELECT CTestID FROM Admin_CTestSettings WHERE AdminCTestSettingID = " + x_ctest_id + " AND Status = 1";
                        };
                        if (!(items.filter(function (x) { return x.ctest_id > 0; }).length > 0)) return [3 /*break*/, 7];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\tINSERT INTO Admin_BatchScheduleCTest (AdminBatchSchID, CTestID, Version, [Order]) \n\t\t\t\t\t\tVALUES " + items
                                .filter(function (x) { return x.ctest_id > 0; })
                                .map(function (x) { return "(\n\t\t\t\t\t\t\t" + batch_id_1 + ",\n\t\t\t\t\t\t\t(" + _ctest_select_1(x.ctest_id) + "),\n\t\t\t\t\t\t\t-1,\n\t\t\t\t\t\t\t" + (x.idx + 1) + "\n\t\t\t\t\t\t)"; })
                                .join(", ") + "\n\t\t\t\t\t;")];
                    case 6:
                        result3 = _g.sent();
                        if (result3.rowsAffected[0] !== items.filter(function (x) { return x.ctest_id > 0; }).length)
                            throw new Error("400.create-failed-due-to-malformed-parameters-settings");
                        _g.label = 7;
                    case 7:
                        if (!(items.filter(function (x) { return x.survey_id > 0; }).length > 0)) return [3 /*break*/, 9];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\tINSERT INTO Admin_BatchScheduleSurvey (AdminBatchSchID, SurveyID, [Order]) \n\t\t\t\t\t\tVALUES " + items
                                .filter(function (x) { return x.survey_id > 0; })
                                .map(function (x) { return "(\n\t\t\t\t\t\t\t" + batch_id_1 + ",\n\t\t\t\t\t\t\t" + x.survey_id + ",\n\t\t\t\t\t\t\t" + (x.idx + 1) + "\n\t\t\t\t\t\t)"; })
                                .join(", ") + "\n\t\t\t\t\t;")];
                    case 8:
                        result4 = _g.sent();
                        if (result4.rowsAffected[0] !== items.filter(function (x) { return x.survey_id > 0; }).length)
                            throw new Error("400.create-failed-due-to-malformed-parameters-settings");
                        _g.label = 9;
                    case 9: 
                    // Return the new ID.
                    return [4 /*yield*/, transaction.commit()];
                    case 10:
                        // Return the new ID.
                        _g.sent();
                        return [2 /*return*/, ActivityRepository._pack_id({
                                group_id: batch_id_1,
                            })];
                    case 11:
                        if (!(object.spec === "lamp.survey") /* survey */) return [3 /*break*/, 20]; /* survey */
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\tINSERT INTO Survey (AdminID, SurveyName) \n\t\t\t\t\tOUTPUT INSERTED.SurveyID\n\t\t\t\t\tVALUES (" + admin_id + ", '" + (object.name ? _escapeMSSQL(object.name) : "new_survey") + "')\n\t\t\t\t;")];
                    case 12:
                        result1 = _g.sent();
                        if (result1.rowsAffected[0] === 0)
                            throw new Error("400.create-failed");
                        survey_id_1 = (_b = Number.parse(result1.recordset[0]["SurveyID"]), (_b !== null && _b !== void 0 ? _b : -1));
                        if (!(Array.isArray(object.settings) && object.settings.length > 0)) return [3 /*break*/, 15];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\tINSERT INTO SurveyQuestions (\n\t\t\t\t\t\t\tSurveyID, QuestionText, AnswerType, \n\t\t\t\t\t\t\tThreshold, Operator, Message\n\t\t\t\t\t\t) \n\t\t\t\t\t\tOUTPUT INSERTED.QuestionID\n\t\t\t\t\t\tVALUES " + object.settings
                                .map(function (q) {
                                var _a, _b, _c, _d, _e, _f;
                                return "(\n\t\t\t\t\t\t\t" + survey_id_1 + ",\n\t\t\t\t\t\t\t'" + _escapeMSSQL(q.text) + "',\n\t\t\t\t\t\t\t" + (["likert", "list", "boolean", "clock", "years", "months", "days", "text"].indexOf(q.type) + 1) + ",\n\t\t\t\t\t\t\t" + (_b = (_a = _opMatch(q.text)) === null || _a === void 0 ? void 0 : _a.tr, (_b !== null && _b !== void 0 ? _b : "NULL")) + ",\n\t\t\t\t\t\t\t" + (_d = (_c = _opMatch(q.text)) === null || _c === void 0 ? void 0 : _c.op, (_d !== null && _d !== void 0 ? _d : "NULL")) + ",\n\t\t\t\t\t\t\t" + (_f = (_e = _opMatch(q.text)) === null || _e === void 0 ? void 0 : _e.msg, (_f !== null && _f !== void 0 ? _f : "NULL")) + "\n\t\t\t\t\t\t)";
                            })
                                .join(", ") + "\n\t\t\t\t\t;")];
                    case 13:
                        result2_1 = _g.sent();
                        if (result2_1.rowsAffected[0] !== object.settings.length)
                            throw new Error("400.create-failed-due-to-malformed-parameters-settings");
                        opts = [].concat.apply([], __spread(object.settings
                            .map(function (x, idx) { return [x.options, idx]; })
                            .filter(function (x) { return !!x[0]; })
                            .map(function (x) { return x[0].map(function (y) { return [y, x[1]]; }); })));
                        if (!(opts.length > 0)) return [3 /*break*/, 15];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\t\tINSERT INTO SurveyQuestionOptions (QuestionID, OptionText) \n\t\t\t\t\t\t\tVALUES " + opts
                                .map(function (q) { return "(\n\t\t\t\t\t\t\t\t" + result2_1.recordset[q[1]]["QuestionID"] + ",\n\t\t\t\t\t\t\t\t'" + _escapeMSSQL(q[0]) + "'\n\t\t\t\t\t\t\t)"; })
                                .join(", ") + "\n\t\t\t\t\t\t;")];
                    case 14:
                        result21 = _g.sent();
                        if (result21.rowsAffected[0] !== opts.length)
                            throw new Error("400.create-failed-due-to-malformed-parameters-settings");
                        _g.label = 15;
                    case 15:
                        if (!(Array.isArray(object.schedule) && object.schedule.length > 0)) return [3 /*break*/, 18];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\tINSERT INTO Admin_SurveySchedule (\n\t\t\t\t\t\t\tAdminID,\n\t\t\t\t\t\t\tSurveyID,\n\t\t\t\t\t\t\tScheduleDate,\n\t\t\t\t\t\t\tTime,\n\t\t\t\t\t\t\tRepeatID\n\t\t\t\t\t\t) \n\t\t\t\t\t\tOUTPUT INSERTED.AdminSurveySchId\n\t\t\t\t\t\tVALUES " + object.schedule
                                .map(function (sched) { return "(\n\t\t\t\t\t\t\t" + admin_id + ", \n\t\t\t\t\t\t\t" + survey_id_1 + ",\n\t\t\t\t\t\t\t'" + sched.start_date + "',\n\t\t\t\t\t\t\t'" + sched.time + "',\n\t\t\t\t\t\t\t" + ([
                                "hourly",
                                "every3h",
                                "every6h",
                                "every12h",
                                "daily",
                                "biweekly",
                                "triweekly",
                                "weekly",
                                "bimonthly",
                                "monthly",
                                "custom",
                                "none",
                            ].indexOf(sched.repeat_interval) + 1) + "\n\t\t\t\t\t\t)"; })
                                .join(", ") + "\n\t\t\t\t\t;")];
                    case 16:
                        result3_1 = _g.sent();
                        if (result3_1.rowsAffected[0] !== object.schedule.length)
                            throw new Error("400.create-failed-due-to-malformed-parameters-schedule");
                        ctime = [].concat.apply([], __spread(object.schedule
                            .map(function (x, idx) { return [x.custom_time, idx]; })
                            .filter(function (x) { return !!x[0]; })
                            .map(function (x) { return x[0].map(function (y) { return [y, x[1]]; }); })));
                        if (!(ctime.length > 0)) return [3 /*break*/, 18];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\t\tINSERT INTO Admin_SurveyScheduleCustomTime (\n\t\t\t\t\t\t\t\tAdminSurveySchId,\n\t\t\t\t\t\t\t\tTime\n\t\t\t\t\t\t\t)\n\t\t\t\t\t\t\tVALUES " + ctime
                                .map(function (x) { return "(\n\t\t\t\t\t\t\t\t" + result3_1.recordset[x[1]]["AdminSurveySchId"] + ",\n\t\t\t\t\t\t\t\t'" + x[0] + "'\n\t\t\t\t\t\t\t)"; })
                                .join(", ") + "\n\t\t\t\t\t\t;")];
                    case 17:
                        result4 = _g.sent();
                        if (result4.rowsAffected[0] === 0)
                            throw new Error("400.create-failed-due-to-malformed-parameters-timing");
                        _g.label = 18;
                    case 18: 
                    // Return the new ID.
                    return [4 /*yield*/, transaction.commit()];
                    case 19:
                        // Return the new ID.
                        _g.sent();
                        return [2 /*return*/, ActivityRepository._pack_id({
                                survey_id: survey_id_1,
                            })];
                    case 20:
                        ctest_id_1 = (_e = (_d = (_c = migrate_1.ActivityIndex.find(function (x) { return x.Name === object.spec; })) === null || _c === void 0 ? void 0 : _c[0]) === null || _d === void 0 ? void 0 : _d.LegacyCTestID, (_e !== null && _e !== void 0 ? _e : -1));
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\tUPDATE Admin_CTestSettings \n\t\t\t\t\tSET Status = 1 \n\t\t\t\t\tWHERE Status = 0\n            AND AdminID = " + admin_id + " \n            AND CTestID = " + ctest_id_1 + "\n          OUTPUT UPDATED.*\n\t\t\t\t;")];
                    case 21:
                        result = _g.sent();
                        if (result.rowsAffected[0] === 0)
                            throw new Error("400.activity-exists-cannot-overwrite");
                        _actual_setting_id = (_f = Number.parse(result.recordset[0]["AdminCTestSettingID"]), (_f !== null && _f !== void 0 ? _f : 0));
                        if (!((ctest_id_1 === 17 || ctest_id_1 === 18) && !!object.settings)) return [3 /*break*/, 23];
                        isA = ctest_id_1 === 17;
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\tMERGE Admin_JewelsTrails" + (isA ? "A" : "B") + "Settings WITH (HOLDLOCK) AS Target\n\t\t\t\t\t\tUSING (VALUES (" + admin_id + ")) AS Source(AdminID)\n\t\t\t\t\t\t\tON Target.AdminID = " + admin_id + "\n\t\t\t\t\t\tWHEN MATCHED THEN\n\t\t\t\t\t\t\tUPDATE SET\n\t\t\t\t\t\t\t\tNoOfSeconds_Beg = " + (object.settings.beginner_seconds || (isA ? 90 : 180)) + ",\n\t\t\t\t\t\t\t\tNoOfSeconds_Int = " + (object.settings.intermediate_seconds || (isA ? 30 : 90)) + ",\n\t\t\t\t\t\t\t\tNoOfSeconds_Adv = " + (object.settings.advanced_seconds || (isA ? 25 : 60)) + ",\n\t\t\t\t\t\t\t\tNoOfSeconds_Exp = " + (object.settings.expert_seconds || (isA ? 15 : 45)) + ",\n\t\t\t\t\t\t\t\tNoOfDiamonds = " + (object.settings.diamond_count || (isA ? 25 : 25)) + ",\n\t\t\t\t\t\t\t\tNoOfShapes = " + (object.settings.shape_count || (isA ? 1 : 2)) + ",\n\t\t\t\t\t\t\t\tNoOfBonusPoints = " + (object.settings.bonus_point_count || (isA ? 50 : 50)) + ",\n\t\t\t\t\t\t\t\tX_NoOfChangesInLevel = " + (object.settings.x_changes_in_level_count || (isA ? 1 : 1)) + ",\n\t\t\t\t\t\t\t\tX_NoOfDiamonds = " + (object.settings.x_diamond_count || (isA ? 1 : 1)) + ",\n\t\t\t\t\t\t\t\tY_NoOfChangesInLevel = " + (object.settings.y_changes_in_level_count || (isA ? 1 : 1)) + ",\n\t\t\t\t\t\t\t\tY_NoOfShapes = " + (object.settings.y_shape_count || (isA ? 1 : 2)) + "\n\t\t\t\t\t\tWHEN NOT MATCHED THEN\n\t\t\t\t\t\t\tINSERT (\n\t\t\t\t\t\t\t\tAdminID, NoOfSeconds_Beg, NoOfSeconds_Int, NoOfSeconds_Adv,\n\t\t\t\t\t\t\t\tNoOfSeconds_Exp, NoOfDiamonds, NoOfShapes, NoOfBonusPoints, \n\t\t\t\t\t\t\t\tX_NoOfChangesInLevel, X_NoOfDiamonds, Y_NoOfChangesInLevel, \n\t\t\t\t\t\t\t\tY_NoOfShapes\n\t\t\t\t\t\t\t) VALUES (\n\t\t\t\t\t\t\t\t" + admin_id + ",\n\t\t\t\t\t\t\t\t" + (object.settings.beginner_seconds || (isA ? 90 : 180)) + ",\n\t\t\t\t\t\t\t\t" + (object.settings.intermediate_seconds || (isA ? 30 : 90)) + ",\n\t\t\t\t\t\t\t\t" + (object.settings.advanced_seconds || (isA ? 25 : 60)) + ",\n\t\t\t\t\t\t\t\t" + (object.settings.expert_seconds || (isA ? 15 : 45)) + ",\n\t\t\t\t\t\t\t\t" + (object.settings.diamond_count || (isA ? 25 : 25)) + ",\n\t\t\t\t\t\t\t\t" + (object.settings.shape_count || (isA ? 1 : 2)) + ",\n\t\t\t\t\t\t\t\t" + (object.settings.bonus_point_count || (isA ? 50 : 50)) + ",\n\t\t\t\t\t\t\t\t" + (object.settings.x_changes_in_level_count || (isA ? 1 : 1)) + ",\n\t\t\t\t\t\t\t\t" + (object.settings.x_diamond_count || (isA ? 1 : 1)) + ",\n\t\t\t\t\t\t\t\t" + (object.settings.y_changes_in_level_count || (isA ? 1 : 1)) + ",\n\t\t\t\t\t\t\t\t" + (object.settings.y_shape_count || (isA ? 1 : 2)) + "\n\t\t\t\t\t\t\t)\n\t\t\t\t\t;")];
                    case 22:
                        result2 = _g.sent();
                        if (result2.rowsAffected[0] === 0)
                            throw new Error("400.create-failed-due-to-malformed-parameters-settings");
                        _g.label = 23;
                    case 23:
                        if (!(Array.isArray(object.schedule) && object.schedule.length > 0)) return [3 /*break*/, 26];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\tINSERT INTO Admin_CTestSchedule (\n\t\t\t\t\t\t\tAdminID,\n\t\t\t\t\t\t\tCTestID,\n\t\t\t\t\t\t\tVersion,\n\t\t\t\t\t\t\tScheduleDate,\n\t\t\t\t\t\t\tTime,\n\t\t\t\t\t\t\tRepeatID\n\t\t\t\t\t\t) \n\t\t\t\t\t\tOUTPUT INSERTED.AdminCTestSchId\n\t\t\t\t\t\tVALUES " + object.schedule
                                .map(function (sched) { return "(\n\t\t\t\t\t\t\t" + admin_id + ", \n\t\t\t\t\t\t\t" + ctest_id_1 + ",\n\t\t\t\t\t\t\t-1,\n\t\t\t\t\t\t\t'" + sched.start_date + "',\n\t\t\t\t\t\t\t'" + sched.time + "',\n\t\t\t\t\t\t\t" + ([
                                "hourly",
                                "every3h",
                                "every6h",
                                "every12h",
                                "daily",
                                "biweekly",
                                "triweekly",
                                "weekly",
                                "bimonthly",
                                "monthly",
                                "custom",
                                "none",
                            ].indexOf(sched.repeat_interval) + 1) + "\n\t\t\t\t\t\t)"; })
                                .join(", ") + "\n\t\t\t\t\t;")];
                    case 24:
                        result3_2 = _g.sent();
                        if (result3_2.rowsAffected[0] !== object.schedule.length)
                            throw new Error("400.create-failed-due-to-malformed-parameters-schedule");
                        ctime = [].concat.apply([], __spread(object.schedule
                            .map(function (x, idx) { return [x.custom_time, idx]; })
                            .filter(function (x) { return !!x[0]; })
                            .map(function (x) { return x[0].map(function (y) { return [y, x[1]]; }); })));
                        if (!(ctime.length > 0)) return [3 /*break*/, 26];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\t\tINSERT INTO Admin_CTestScheduleCustomTime (\n\t\t\t\t\t\t\t\tAdminCTestSchId,\n\t\t\t\t\t\t\t\tTime\n\t\t\t\t\t\t\t)\n\t\t\t\t\t\t\tVALUES " + ctime
                                .map(function (x) { return "(\n\t\t\t\t\t\t\t\t" + result3_2.recordset[x[1]]["AdminCTestSchId"] + ",\n\t\t\t\t\t\t\t\t'" + x[0] + "'\n\t\t\t\t\t\t\t)"; })
                                .join(", ") + "\n\t\t\t\t\t\t;")];
                    case 25:
                        result4 = _g.sent();
                        if (result4.rowsAffected[0] === 0)
                            throw new Error("400.create-failed-due-to-malformed-parameters-timing");
                        _g.label = 26;
                    case 26: 
                    // Return the new ID.
                    return [4 /*yield*/, transaction.commit()];
                    case 27:
                        // Return the new ID.
                        _g.sent();
                        return [2 /*return*/, ActivityRepository._pack_id({
                                ctest_id: _actual_setting_id,
                            })];
                    case 28: return [3 /*break*/, 31];
                    case 29:
                        e_1 = _g.sent();
                        return [4 /*yield*/, transaction.rollback()];
                    case 30:
                        _g.sent();
                        throw e_1;
                    case 31: throw new Error("400.creation-failed");
                }
            });
        });
    };
    /**
     * Update an `Activity` with new fields.
     */
    ActivityRepository._update = function (
    /**
     * The Activity's ID.
     */
    activity_id, 
    /**
     * The object containing partial updating fields.
     */
    object) {
        var _a, _b;
        return __awaiter(this, void 0, void 0, function () {
            var _c, ctest_id, survey_id, group_id, transaction, result, result1, ctime, result2, items, ctest, survey, _ctest_select_2, result3, result4, result0, result0, result2, result3_3, ctime, result4, result3, result2_2, opts, result21, result, result2, result3_4, ctime, result4, checkJewels, adminID, actualID, isA, result2, e_2;
            return __generator(this, function (_d) {
                switch (_d.label) {
                    case 0:
                        _c = ActivityRepository._unpack_id(activity_id), ctest_id = _c.ctest_id, survey_id = _c.survey_id, group_id = _c.group_id;
                        if (typeof object.spec === "string")
                            throw new Error("400.update-failed-modifying-activityspec-is-illegal");
                        transaction = app_1.SQL.transaction();
                        return [4 /*yield*/, transaction.begin()];
                    case 1:
                        _d.sent();
                        _d.label = 2;
                    case 2:
                        _d.trys.push([2, 36, , 38]);
                        if (!(group_id > 0) /* group */) return [3 /*break*/, 11]; /* group */
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\tSELECT AdminBatchSchID \n\t\t\t\t\tFROM Admin_BatchSchedule \n\t\t\t\t\tWHERE IsDeleted = 0\n\t\t\t\t\t\tAND AdminBatchSchID = " + group_id + "\n\t\t\t\t;")];
                    case 3:
                        result = _d.sent();
                        if (result.recordset.length === 0)
                            throw new Error("404.object-not-found");
                        if (!(Array.isArray(object.schedule) || typeof object.name === "string")) return [3 /*break*/, 6];
                        if (Array.isArray(object.schedule) && object.schedule.length !== 1)
                            throw new Error("400.empty-duration-unsupported");
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\tUPDATE Admin_BatchSchedule SET \n\t\t\t\t\t\t\t" + [
                                !!object.name ? "BatchName = '" + object.name + "'" : null,
                                !!object.schedule[0].start_date ? "ScheduleDate = '" + object.schedule[0].start_date + "'" : null,
                                !!object.schedule[0].time ? "Time = '" + object.schedule[0].time + "'" : null,
                                !!object.schedule[0].repeat_interval
                                    ? "RepeatID = " + ([
                                        "hourly",
                                        "every3h",
                                        "every6h",
                                        "every12h",
                                        "daily",
                                        "biweekly",
                                        "triweekly",
                                        "weekly",
                                        "bimonthly",
                                        "monthly",
                                        "custom",
                                        "none",
                                    ].indexOf(object.schedule[0].repeat_interval) + 1)
                                    : null,
                            ]
                                .filter(function (x) { return x !== null; })
                                .join(", ") + "\n\t\t\t\t\t\tWHERE IsDeleted = 0\n\t\t\t\t\t\t\tAND AdminBatchSchID = " + group_id + "\n\t\t\t\t\t;")
                            // Modify custom times.
                        ];
                    case 4:
                        result1 = _d.sent();
                        if (!Array.isArray(object.schedule[0].custom_time)) return [3 /*break*/, 6];
                        ctime = object.schedule[0].custom_time;
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\t\tMERGE INTO Admin_BatchScheduleCustomTime Target\n\t\t\t\t\t\t\tUSING (VALUES\n\t\t\t\t\t\t\t\t" + (ctime.length === 0 ? "(NULL, NULL)" : ctime.map(function (x) { return "(" + group_id + ", '" + _escapeMSSQL(x) + "')"; }).join(", ")) + "\n\t\t\t\t\t\t\t) AS Source(AdminBatchSchID, Time)\n\t\t\t\t\t\t\t\tON Target.AdminBatchSchID = Source.AdminBatchSchID \n\t\t\t\t\t\t\t\tAND Target.Time = Source.Time\n\t\t\t\t\t\t\tWHEN NOT MATCHED BY Target THEN\n\t\t\t\t\t\t\t\tINSERT (AdminBatchSchID, Time) \n\t\t\t\t\t\t\t\tVALUES (Source.AdminBatchSchID, Source.Time)\n\t\t\t\t\t\t\tWHEN NOT MATCHED BY Source AND Target.AdminBatchSchID = " + group_id + " THEN \n\t\t\t\t\t\t\t\tDELETE\n\t\t\t\t\t\t\tOUTPUT $ACTION, INSERTED.*, DELETED.*\n\t\t\t\t\t\t;")];
                    case 5:
                        result2 = _d.sent();
                        _d.label = 6;
                    case 6:
                        if (!Array.isArray(object.settings)) return [3 /*break*/, 9];
                        items = object.settings.map(function (x, idx) { return (__assign(__assign({}, ActivityRepository._unpack_id(x)), { idx: idx })); });
                        if (items.filter(function (x) { return x.group_id > 0; }).length > 0)
                            throw new Error("400.nested-activity-groups-unsupported");
                        if (items.filter(function (x) { return x.group_id === 0; }).length === 0)
                            throw new Error("400.empty-activity-group-settings-array-unsupported");
                        ctest = items.filter(function (x) { return x.ctest_id > 0; });
                        survey = items.filter(function (x) { return x.survey_id > 0; });
                        _ctest_select_2 = "SELECT CTestID FROM Admin_CTestSettings WHERE AdminCTestSettingID = " + ctest_id;
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\tMERGE INTO Admin_BatchScheduleCTest Target\n\t\t\t\t\t\tUSING (VALUES\n\t\t\t\t\t\t\t" + (ctest.length === 0
                                ? "(NULL, NULL, NULL)"
                                : ctest.map(function (x) { return "(" + group_id + ", (" + _ctest_select_2 + "), " + (x.idx + 1) + ")"; }).join(", ")) + "\n\t\t\t\t\t\t) AS Source(AdminBatchSchID, CTestID, [Order])\n\t\t\t\t\t\t\tON Target.AdminBatchSchID = Source.AdminBatchSchID \n\t\t\t\t\t\t\tAND Target.CTestID = Source.CTestID\n\t\t\t\t\t\t\tAND Target.[Order] = Source.[Order]\n\t\t\t\t\t\t\tAND Source.AdminBatchSchID IS NOT NULL\n\t\t\t\t\t\tWHEN NOT MATCHED BY Target THEN\n\t\t\t\t\t\t\tINSERT (AdminBatchSchID, CTestID, Version, [Order]) \n\t\t\t\t\t\t\tVALUES (Source.AdminBatchSchID, Source.CTestID, -1, Source.[Order])\n\t\t\t\t\t\tWHEN NOT MATCHED BY Source AND Target.AdminBatchSchID = " + group_id + " THEN \n\t\t\t\t\t\t\tDELETE\n\t\t\t\t\t\tOUTPUT $ACTION, INSERTED.*, DELETED.*\n\t\t\t\t\t;")];
                    case 7:
                        result3 = _d.sent();
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\tMERGE INTO Admin_BatchScheduleSurvey Target\n\t\t\t\t\t\tUSING (VALUES\n\t\t\t\t\t\t\t" + (survey.length === 0
                                ? "(NULL, NULL, NULL)"
                                : survey.map(function (x) { return "(" + group_id + ", " + x.survey_id + ", " + (x.idx + 1) + ")"; }).join(", ")) + "\n\t\t\t\t\t\t) AS Source(AdminBatchSchID, SurveyID, [Order])\n\t\t\t\t\t\t\tON Target.AdminBatchSchID = Source.AdminBatchSchID \n\t\t\t\t\t\t\tAND Target.SurveyID = Source.SurveyID\n\t\t\t\t\t\t\tAND Target.[Order] = Source.[Order]\n\t\t\t\t\t\t\tAND Source.AdminBatchSchID IS NOT NULL\n\t\t\t\t\t\tWHEN NOT MATCHED BY Target THEN\n\t\t\t\t\t\t\tINSERT (AdminBatchSchID, SurveyID, [Order]) \n\t\t\t\t\t\t\tVALUES (Source.AdminBatchSchID, Source.SurveyID, Source.[Order])\n\t\t\t\t\t\tWHEN NOT MATCHED BY Source AND Target.AdminBatchSchID = " + group_id + " THEN \n\t\t\t\t\t\t\tDELETE\n\t\t\t\t\t\tOUTPUT $ACTION, INSERTED.*, DELETED.*\n\t\t\t\t\t;")];
                    case 8:
                        result4 = _d.sent();
                        _d.label = 9;
                    case 9: return [4 /*yield*/, transaction.commit()];
                    case 10:
                        _d.sent();
                        return [2 /*return*/, {}];
                    case 11:
                        if (!(survey_id > 0) /* survey */) return [3 /*break*/, 25]; /* survey */
                        if (!(typeof object.name === "string")) return [3 /*break*/, 13];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\tUPDATE Survey SET \n\t\t\t\t\t\t\tSurveyName = '" + _escapeMSSQL(object.name) + "'\n\t\t\t\t\t\tWHERE IsDeleted = 0\n\t\t\t\t\t\t\tAND SurveyID = " + survey_id + "\n\t\t\t\t\t;")];
                    case 12:
                        result0 = _d.sent();
                        if (result0.rowsAffected[0] === 0)
                            throw new Error("404.object-not-found");
                        return [3 /*break*/, 15];
                    case 13: return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\tSELECT SurveyID \n\t\t\t\t\t\tFROM Survey \n\t\t\t\t\t\tWHERE IsDeleted = 0\n\t\t\t\t\t\t\tAND SurveyID = " + survey_id + "\n\t\t\t\t\t;")];
                    case 14:
                        result0 = _d.sent();
                        if (result0.recordset.length === 0)
                            throw new Error("404.object-not-found");
                        _d.label = 15;
                    case 15:
                        if (!Array.isArray(object.schedule)) return [3 /*break*/, 19];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\tUPDATE Admin_SurveySchedule \n\t\t\t\t\t\tSET IsDeleted = 1\n\t\t\t\t\t\tWHERE IsDeleted = 0\n\t\t\t\t\t\t\tAND SurveyID = " + survey_id + "\n\t\t\t\t\t;")];
                    case 16:
                        result2 = _d.sent();
                        if (!(object.schedule.length > 0)) return [3 /*break*/, 19];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\t\tINSERT INTO Admin_SurveySchedule (\n\t\t\t\t\t\t\t\tAdminID,\n\t\t\t\t\t\t\t\tSurveyID,\n\t\t\t\t\t\t\t\tScheduleDate,\n\t\t\t\t\t\t\t\tTime,\n\t\t\t\t\t\t\t\tRepeatID\n\t\t\t\t\t\t\t) \n\t\t\t\t\t\t\tOUTPUT INSERTED.AdminSurveySchId\n\t\t\t\t\t\t\tVALUES " + object.schedule
                                .map(function (sched) { return "(\n\t\t\t\t\t\t\t\t(\n                  SELECT AdminID\n                  FROM Survey\n                  WHERE SurveyID = " + survey_id + "\n\t\t\t\t\t\t\t  ),\n\t\t\t\t\t\t\t\t" + survey_id + ",\n\t\t\t\t\t\t\t\t'" + sched.start_date + "',\n\t\t\t\t\t\t\t\t'" + sched.time + "',\n\t\t\t\t\t\t\t\t" + ([
                                "hourly",
                                "every3h",
                                "every6h",
                                "every12h",
                                "daily",
                                "biweekly",
                                "triweekly",
                                "weekly",
                                "bimonthly",
                                "monthly",
                                "custom",
                                "none",
                            ].indexOf(sched.repeat_interval) + 1) + "\n\t\t\t\t\t\t\t)"; })
                                .join(", ") + "\n\t\t\t\t\t\t;")];
                    case 17:
                        result3_3 = _d.sent();
                        if (result3_3.rowsAffected[0] !== object.schedule.length)
                            throw new Error("400.create-failed-due-to-malformed-parameters-schedule");
                        ctime = [].concat.apply([], __spread(object.schedule
                            .map(function (x, idx) { return [x.custom_time, idx]; })
                            .filter(function (x) { return !!x[0]; })
                            .map(function (x) { return x[0].map(function (y) { return [y, x[1]]; }); })));
                        if (!(ctime.length > 0)) return [3 /*break*/, 19];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\t\t\tINSERT INTO Admin_SurveyScheduleCustomTime (\n\t\t\t\t\t\t\t\t\tAdminSurveySchId,\n\t\t\t\t\t\t\t\t\tTime\n\t\t\t\t\t\t\t\t)\n\t\t\t\t\t\t\t\tVALUES " + ctime
                                .map(function (x) { return "(\n\t\t\t\t\t\t\t\t\t" + result3_3.recordset[x[1]]["AdminSurveySchId"] + ",\n\t\t\t\t\t\t\t\t\t'" + x[0] + "'\n\t\t\t\t\t\t\t\t)"; })
                                .join(", ") + "\n\t\t\t\t\t\t\t;")];
                    case 18:
                        result4 = _d.sent();
                        if (result4.rowsAffected[0] === 0)
                            throw new Error("400.create-failed-due-to-malformed-parameters-timing");
                        _d.label = 19;
                    case 19:
                        if (!Array.isArray(object.settings)) return [3 /*break*/, 23];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\tUPDATE SurveyQuestions \n\t\t\t\t\t\tSET IsDeleted = 1\n\t\t\t\t\t\tWHERE IsDeleted = 0\n\t\t\t\t\t\t\tAND SurveyID = " + survey_id + "\n\t\t\t\t\t;")];
                    case 20:
                        result3 = _d.sent();
                        if (!(object.settings.length > 0)) return [3 /*break*/, 23];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\t\tINSERT INTO SurveyQuestions (SurveyID, QuestionText, AnswerType) \n\t\t\t\t\t\t\tOUTPUT INSERTED.QuestionID\n\t\t\t\t\t\t\tVALUES " + object.settings
                                .map(function (q) { return "(\n\t\t\t\t\t\t\t\t" + survey_id + ",\n\t\t\t\t\t\t\t\t'" + _escapeMSSQL(q.text) + "',\n\t\t\t\t\t\t\t\t" + (["likert", "list", "boolean", "clock", "years", "months", "days", "text"].indexOf(q.type) + 1) + "\n\t\t\t\t\t\t\t)"; })
                                .join(", ") + "\n\t\t\t\t\t\t;")];
                    case 21:
                        result2_2 = _d.sent();
                        if (result2_2.rowsAffected[0] !== object.settings.length)
                            throw new Error("400.create-failed-due-to-malformed-parameters-settings");
                        opts = [].concat.apply([], __spread(object.settings
                            .map(function (x, idx) { return [x.options, idx]; })
                            .filter(function (x) { return !!x[0]; })
                            .map(function (x) { return x[0].map(function (y) { return [y, x[1]]; }); })));
                        if (!(opts.length > 0)) return [3 /*break*/, 23];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\t\t\tINSERT INTO SurveyQuestionOptions (QuestionID, OptionText) \n\t\t\t\t\t\t\t\tVALUES " + opts
                                .map(function (q) { return "(\n\t\t\t\t\t\t\t\t\t" + result2_2.recordset[q[1]]["QuestionID"] + ",\n\t\t\t\t\t\t\t\t\t'" + _escapeMSSQL(q[0]) + "'\n\t\t\t\t\t\t\t\t)"; })
                                .join(", ") + "\n\t\t\t\t\t\t\t;")];
                    case 22:
                        result21 = _d.sent();
                        if (result21.rowsAffected[0] !== opts.length)
                            throw new Error("400.create-failed-due-to-malformed-parameters-settings");
                        _d.label = 23;
                    case 23: return [4 /*yield*/, transaction.commit()];
                    case 24:
                        _d.sent();
                        return [2 /*return*/, {}];
                    case 25: return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\tSELECT AdminID, CTestID \n\t\t\t\t\tFROM Admin_CTestSettings \n\t\t\t\t\tWHERE Status = 1\n\t\t\t\t\t\tAND AdminCTestSettingID = " + ctest_id + "\n\t\t\t\t;")];
                    case 26:
                        result = _d.sent();
                        if (result.recordset.length === 0)
                            throw new Error("404.object-not-found");
                        if (!Array.isArray(object.schedule)) return [3 /*break*/, 30];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\tUPDATE Admin_CTestSchedule \n\t\t\t\t\t\tSET IsDeleted = 1\n\t\t\t\t\t\tWHERE IsDeleted = 0\n\t\t\t\t\t\t\tAND AdminID IN (\n\t\t\t\t\t\t\t\tSELECT AdminID\n\t\t\t\t\t\t\t\tFROM Admin_CTestSettings\n\t\t\t\t\t\t\t\tWHERE AdminCTestSettingID = " + ctest_id + "\n\t\t\t\t\t\t\t)\n\t\t\t\t\t\t\tAND CTestID IN (\n\t\t\t\t\t\t\t\tSELECT CTestID\n\t\t\t\t\t\t\t\tFROM Admin_CTestSettings\n\t\t\t\t\t\t\t\tWHERE AdminCTestSettingID = " + ctest_id + "\n\t\t\t\t\t\t\t)\n\t\t\t\t\t;")];
                    case 27:
                        result2 = _d.sent();
                        if (!(object.schedule.length > 0)) return [3 /*break*/, 30];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\t\tINSERT INTO Admin_CTestSchedule (\n\t\t\t\t\t\t\t\tAdminID,\n\t\t\t\t\t\t\t\tCTestID,\n\t\t\t\t\t\t\t\tVersion,\n\t\t\t\t\t\t\t\tScheduleDate,\n\t\t\t\t\t\t\t\tTime,\n\t\t\t\t\t\t\t\tRepeatID\n\t\t\t\t\t\t\t) \n\t\t\t\t\t\t\tOUTPUT INSERTED.AdminCTestSchId\n\t\t\t\t\t\t\tVALUES " + object.schedule
                                .map(function (sched) { return "(\n\t\t\t\t\t\t\t\t(\n                  SELECT AdminID\n                  FROM Admin_CTestSettings\n                  WHERE AdminCTestSettingID = " + ctest_id + "\n\t\t\t\t\t\t\t  ), \n\t\t\t\t\t\t\t\t(\n                  SELECT CTestID\n                  FROM Admin_CTestSettings\n                  WHERE AdminCTestSettingID = " + ctest_id + "\n\t\t\t\t\t\t\t  ), \n\t\t\t\t\t\t\t\t-1,\n\t\t\t\t\t\t\t\t'" + sched.start_date + "',\n\t\t\t\t\t\t\t\t'" + sched.time + "',\n\t\t\t\t\t\t\t\t" + ([
                                "hourly",
                                "every3h",
                                "every6h",
                                "every12h",
                                "daily",
                                "biweekly",
                                "triweekly",
                                "weekly",
                                "bimonthly",
                                "monthly",
                                "custom",
                                "none",
                            ].indexOf(sched.repeat_interval) + 1) + "\n\t\t\t\t\t\t\t)"; })
                                .join(", ") + "\n\t\t\t\t\t\t;")];
                    case 28:
                        result3_4 = _d.sent();
                        if (result3_4.rowsAffected[0] !== object.schedule.length)
                            throw new Error("400.create-failed-due-to-malformed-parameters-schedule");
                        ctime = [].concat.apply([], __spread(object.schedule
                            .map(function (x, idx) { return [x.custom_time, idx]; })
                            .filter(function (x) { return !!x[0]; })
                            .map(function (x) { return x[0].map(function (y) { return [y, x[1]]; }); })));
                        if (!(ctime.length > 0)) return [3 /*break*/, 30];
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\t\t\tINSERT INTO Admin_CTestScheduleCustomTime (\n\t\t\t\t\t\t\t\t\tAdminCTestSchId,\n\t\t\t\t\t\t\t\t\tTime\n\t\t\t\t\t\t\t\t)\n\t\t\t\t\t\t\t\tVALUES " + ctime
                                .map(function (x) { return "(\n\t\t\t\t\t\t\t\t\t" + result3_4.recordset[x[1]]["AdminCTestSchId"] + ",\n\t\t\t\t\t\t\t\t\t'" + x[0] + "'\n\t\t\t\t\t\t\t\t)"; })
                                .join(", ") + "\n\t\t\t\t\t\t\t;")];
                    case 29:
                        result4 = _d.sent();
                        if (result4.rowsAffected[0] === 0)
                            throw new Error("400.create-failed-due-to-malformed-parameters-timing");
                        _d.label = 30;
                    case 30: return [4 /*yield*/, transaction.request().query("\n            SELECT AdminID, CTestID \n            FROM Admin_CTestSettings\n            WHERE AdminCTestSettingID = " + ctest_id + "\n          ;")];
                    case 31:
                        checkJewels = _d.sent();
                        adminID = (_a = Number.parse(checkJewels.recordset[0]["AdminID"]), (_a !== null && _a !== void 0 ? _a : 0));
                        actualID = (_b = Number.parse(checkJewels.recordset[0]["CTestID"]), (_b !== null && _b !== void 0 ? _b : 0));
                        if (!(typeof object.settings === "object" && (actualID === 17 || actualID === 18))) return [3 /*break*/, 33];
                        isA = actualID === 17;
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\t\tUPDATE Admin_JewelsTrails" + (isA ? "A" : "B") + "Settings SET\n\t\t\t\t\t\t\tNoOfSeconds_Beg = " + (object.settings.beginner_seconds || (isA ? 90 : 180)) + ",\n\t\t\t\t\t\t\tNoOfSeconds_Int = " + (object.settings.intermediate_seconds || (isA ? 30 : 90)) + ",\n\t\t\t\t\t\t\tNoOfSeconds_Adv = " + (object.settings.advanced_seconds || (isA ? 25 : 60)) + ",\n\t\t\t\t\t\t\tNoOfSeconds_Exp = " + (object.settings.expert_seconds || (isA ? 15 : 45)) + ",\n\t\t\t\t\t\t\tNoOfDiamonds = " + (object.settings.diamond_count || (isA ? 25 : 25)) + ",\n\t\t\t\t\t\t\tNoOfShapes = " + (object.settings.shape_count || (isA ? 1 : 2)) + ",\n\t\t\t\t\t\t\tNoOfBonusPoints = " + (object.settings.bonus_point_count || (isA ? 50 : 50)) + ",\n\t\t\t\t\t\t\tX_NoOfChangesInLevel = " + (object.settings.x_changes_in_level_count || (isA ? 1 : 1)) + ",\n\t\t\t\t\t\t\tX_NoOfDiamonds = " + (object.settings.x_diamond_count || (isA ? 1 : 1)) + ",\n\t\t\t\t\t\t\tY_NoOfChangesInLevel = " + (object.settings.y_changes_in_level_count || (isA ? 1 : 1)) + ",\n\t\t\t\t\t\t\tY_NoOfShapes = " + (object.settings.y_shape_count || (isA ? 1 : 2)) + "\n\t\t\t\t\t\tWHERE Admin_JewelsTrails" + (isA ? "A" : "B") + "Settings.AdminID = " + adminID + "\n\t\t\t\t\t;")];
                    case 32:
                        result2 = _d.sent();
                        if (result2.rowsAffected[0] === 0)
                            throw new Error("400.create-failed-due-to-malformed-parameters-settings");
                        _d.label = 33;
                    case 33: return [4 /*yield*/, transaction.commit()];
                    case 34:
                        _d.sent();
                        return [2 /*return*/, {}];
                    case 35: return [3 /*break*/, 38];
                    case 36:
                        e_2 = _d.sent();
                        return [4 /*yield*/, transaction.rollback()];
                    case 37:
                        _d.sent();
                        throw e_2;
                    case 38: throw new Error("400.update-failed");
                }
            });
        });
    };
    /**
     * Delete an `Activity` row.
     */
    ActivityRepository._delete = function (
    /**
     * The Activity's ID.
     */
    activity_id) {
        return __awaiter(this, void 0, void 0, function () {
            var _a, ctest_id, survey_id, group_id, transaction, result, result, result2, result3, result1, result2, e_3;
            return __generator(this, function (_b) {
                switch (_b.label) {
                    case 0:
                        _a = ActivityRepository._unpack_id(activity_id), ctest_id = _a.ctest_id, survey_id = _a.survey_id, group_id = _a.group_id;
                        transaction = app_1.SQL.transaction();
                        return [4 /*yield*/, transaction.begin()];
                    case 1:
                        _b.sent();
                        _b.label = 2;
                    case 2:
                        _b.trys.push([2, 13, , 15]);
                        if (!(group_id > 0) /* group */) return [3 /*break*/, 4]; /* group */
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\tUPDATE Admin_BatchSchedule \n\t\t\t\t\tSET IsDeleted = 1 \n\t\t\t\t\tWHERE IsDeleted = 0\n\t\t\t\t\t\tAND AdminBatchSchID = " + group_id + "\n\t\t\t\t;")];
                    case 3:
                        result = _b.sent();
                        if (result.rowsAffected[0] === 0)
                            throw new Error("404.object-not-found");
                        return [3 /*break*/, 11];
                    case 4:
                        if (!(survey_id > 0) /* survey */) return [3 /*break*/, 8]; /* survey */
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\tUPDATE Survey \n\t\t\t\t\tSET IsDeleted = 1 \n\t\t\t\t\tWHERE IsDeleted = 0 \n\t\t\t\t\t\tAND SurveyID = " + survey_id + "\n\t\t\t\t;")];
                    case 5:
                        result = _b.sent();
                        if (result.rowsAffected[0] === 0)
                            throw new Error("404.object-not-found");
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\tUPDATE Admin_SurveySchedule \n\t\t\t\t\tSET IsDeleted = 1\n\t\t\t\t\tWHERE IsDeleted = 0\n\t\t\t\t\t\tAND SurveyID = " + survey_id + "\n\t\t\t\t;")];
                    case 6:
                        result2 = _b.sent();
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\tUPDATE SurveyQuestions \n\t\t\t\t\tSET IsDeleted = 1\n\t\t\t\t\tWHERE IsDeleted = 0\n\t\t\t\t\t\tAND SurveyID = " + survey_id + "\n\t\t\t\t;")];
                    case 7:
                        result3 = _b.sent();
                        return [3 /*break*/, 11];
                    case 8: return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\tUPDATE Admin_CTestSettings \n\t\t\t\t\tSET Status = 0 \n\t\t\t\t\tWHERE Status = 1\n\t\t\t\t\t\tAND AdminCTestSettingID = " + ctest_id + "\n\t\t\t\t;")];
                    case 9:
                        result1 = _b.sent();
                        if (result1.rowsAffected[0] === 0)
                            throw new Error("404.object-not-found");
                        return [4 /*yield*/, transaction.request().query("\n\t\t\t\t\tUPDATE Admin_CTestSchedule \n\t\t\t\t\tSET IsDeleted = 1\n\t\t\t\t\tWHERE IsDeleted = 0\n\t\t\t\t\t\tAND AdminID IN (\n\t\t\t\t\t\t\tSELECT AdminID\n\t\t\t\t\t\t\tFROM Admin_CTestSettings\n\t\t\t\t\t\t\tWHERE AdminCTestSettingID = " + ctest_id + "\n\t\t\t\t\t\t)\n\t\t\t\t\t\tAND CTestID IN (\n\t\t\t\t\t\t\tSELECT CTestID\n\t\t\t\t\t\t\tFROM Admin_CTestSettings\n\t\t\t\t\t\t\tWHERE AdminCTestSettingID = " + ctest_id + "\n\t\t\t\t\t\t)\n\t\t\t\t;")];
                    case 10:
                        result2 = _b.sent();
                        _b.label = 11;
                    case 11: return [4 /*yield*/, transaction.commit()];
                    case 12:
                        _b.sent();
                        return [2 /*return*/, {}];
                    case 13:
                        e_3 = _b.sent();
                        return [4 /*yield*/, transaction.rollback()];
                    case 14:
                        _b.sent();
                        throw e_3;
                    case 15: throw new Error("400.delete-failed");
                }
            });
        });
    };
    return ActivityRepository;
}());
exports.ActivityRepository = ActivityRepository;
var spec_map = {
    "lamp.group": "Activity Group",
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
var _escapeMSSQL = function (val) {
    return val.replace(/[\0\n\r\b\t\\'"\x1a]/g, function (s) {
        switch (s) {
            case "\0":
                return "\\0";
            case "\n":
                return "\\n";
            case "\r":
                return "\\r";
            case "\b":
                return "\\b";
            case "\t":
                return "\\t";
            case "\x1a":
                return "\\Z";
            case "'":
                return "''";
            case '"':
                return '""';
            default:
                return "\\" + s;
        }
    });
};
// threshold & operator hard-coded matches
var _opMatch = function (val) {
    return ({
        "Today I have thoughts of self-harm": {
            tr: 1,
            op: "'1'",
            msg: "'Please remember that this app is not monitored.  If you are having thoughts of suicide or self-harm, please call 1-800-273-8255.'",
        },
    }[val]);
};
/**
 * Produce the internal-only Jewels A/B settings mappings.
 * Note: this is not to be exposed externally as an API.
 *
 * The column map specifies the LAMP object key to DB row column mapping.
 * The default map specifies the LAMP object's value if none is found.
 */
function jewelsMap(
/**
 * The settings key to produce detail on.
 */
key, 
/**
 * Either false for column mapping, or true for defaults mapping.
 */
variety) {
    if (variety === void 0) { variety = false; }
    return (!variety
        ? {
            beginner_seconds: "NoOfSeconds_Beg",
            intermediate_seconds: "NoOfSeconds_Int",
            advanced_seconds: "NoOfSeconds_Adv",
            expert_seconds: "NoOfSeconds_Exp",
            diamond_count: "NoOfDiamonds",
            shape_count: "NoOfShapes",
            bonus_point_count: "NoOfBonusPoints",
            x_changes_in_level_count: "X_NoOfChangesInLevel",
            x_diamond_count: "X_NoOfDiamonds",
            y_changes_in_level_count: "Y_NoOfChangesInLevel",
            y_shape_count: "Y_NoOfShapes",
        }
        : {
            beginner_seconds: 0,
            intermediate_seconds: 0,
            advanced_seconds: 0,
            expert_seconds: 0,
            diamond_count: 0,
            shape_count: 0,
            bonus_point_count: 0,
            x_changes_in_level_count: 0,
            x_diamond_count: 0,
            y_changes_in_level_count: 0,
            y_shape_count: 0,
        })[key];
}
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiQWN0aXZpdHlSZXBvc2l0b3J5LmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vc3JjL3JlcG9zaXRvcnkvcG91Y2hSZXBvc2l0b3J5L0FjdGl2aXR5UmVwb3NpdG9yeS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7QUFBQSxpQ0FBaUQ7QUFFakQsaURBQStDO0FBRS9DLDJDQUF5QztBQUN6QyxxREFBbUQ7QUFHbkQsOEVBQTRFO0FBQzVFLG9FQUFrRTtBQUVsRSxrRUFBZ0U7QUFDaEUsa0VBQW9GO0FBQ3BGLHFDQUF5QztBQUV6QztJQUFBO0lBK3pDQSxDQUFDO0lBOXpDQzs7T0FFRztJQUNXLDJCQUFRLEdBQXRCLFVBQXVCLFVBZXRCO1FBQ0MsT0FBTyxnQ0FBZSxDQUFDO1lBQ2YsbUJBQVMsQ0FBQyxJQUFJO1lBQ3BCLFVBQVUsQ0FBQyxRQUFRLElBQUksQ0FBQztZQUN4QixVQUFVLENBQUMsU0FBUyxJQUFJLENBQUM7WUFDekIsVUFBVSxDQUFDLFFBQVEsSUFBSSxDQUFDO1NBQ3pCLENBQUMsQ0FBQTtJQUNKLENBQUM7SUFFRDs7T0FFRztJQUNXLDZCQUFVLEdBQXhCLFVBQ0UsRUFBVTtRQWlCVixJQUFNLFVBQVUsR0FBRyxrQ0FBaUIsQ0FBQyxFQUFFLENBQUMsQ0FBQTtRQUN4QyxJQUFJLFVBQVUsQ0FBQyxDQUFDLENBQUMsS0FBVyxtQkFBUyxDQUFDLElBQUk7WUFBRSxNQUFNLElBQUksS0FBSyxDQUFDLHdCQUF3QixDQUFDLENBQUE7UUFDckYsSUFBTSxNQUFNLEdBQUcsVUFBVSxDQUFDLEtBQUssQ0FBQyxDQUFDLENBQUMsQ0FBQyxHQUFHLENBQUMsVUFBQyxDQUFDLHdCQUFLLE1BQU0sQ0FBQyxLQUFLLENBQUMsQ0FBQyxDQUFDLHVDQUFJLENBQUMsSUFBQSxDQUFDLENBQUE7UUFDbkUsT0FBTztZ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