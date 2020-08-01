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
var Activity_1 = require("../model/Activity");
var Study_1 = require("../model/Study");
var Researcher_1 = require("../model/Researcher");
var ResearcherRepository_1 = require("../repository/ResearcherRepository");
var StudyRepository_1 = require("../repository/StudyRepository");
var TypeRepository_1 = require("../repository/TypeRepository");
var TypeRepository_2 = require("../repository/TypeRepository");
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiQWN0aXZpdHlSZXBvc2l0b3J5LmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vc3JjL3JlcG9zaXRvcnkvQWN0aXZpdHlSZXBvc2l0b3J5LnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUFBLDhCQUE4QztBQUU5Qyw4Q0FBNEM7QUFFNUMsd0NBQXNDO0FBQ3RDLGtEQUFnRDtBQUdoRCwyRUFBeUU7QUFDekUsaUVBQStEO0FBRS9ELCtEQUE2RDtBQUM3RCwrREFBaUY7QUFDakYscUNBQXlDO0FBRXpDO0lBQUE7SUErekNBLENBQUM7SUE5ekNDOztPQUVHO0lBQ1csMkJBQVEsR0FBdEIsVUFBdUIsVUFldEI7UUFDQyxPQUFPLGdDQUFlLENBQUM7WUFDZixtQkFBUyxDQUFDLElBQUk7WUFDcEIsVUFBVSxDQUFDLFFBQVEsSUFBSSxDQUFDO1lBQ3hCLFVBQVUsQ0FBQyxTQUFTLElBQUksQ0FBQztZQUN6QixVQUFVLENBQUMsUUFBUSxJQUFJLENBQUM7U0FDekIsQ0FBQyxDQUFBO0lBQ0osQ0FBQztJQUVEOztPQUVHO0lBQ1csNkJBQVUsR0FBeEIsVUFDRSxFQUFVO1FBaUJWLElBQU0sVUFBVSxHQUFHLGtDQUFpQixDQUFDLEVBQUUsQ0FBQyxDQUFBO1FBQ3hDLElBQUksVUFBVSxDQUFDLENBQUMsQ0FBQyxLQUFXLG1CQUFTLENBQUMsSUFBSTtZQUFFLE1BQU0sSUFBSSxLQUFLLENBQUMsd0JBQXdCLENBQUMsQ0FBQTtRQUNyRixJQUFNLE1BQU0sR0FBRyxVQUFVLENBQUMsS0FBSyxDQUFDLENBQUMsQ0FBQyxDQUFDLEdBQUcsQ0FBQyxVQUFDLENBQUMsd0JBQUssTUFBTSxDQUFDLEtBQUssQ0FBQyxDQUFDLENBQUMsdUNBQUksQ0FBQyxJQUFBLENBQUMsQ0FBQTtRQUNuRSxPQUFPO1lBQ0wsUUFBUSxFQUFFLE1BQU0s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