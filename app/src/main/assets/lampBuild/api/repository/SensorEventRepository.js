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
Object.defineProperty(exports, "__esModule", { value: true });
var app_1 = require("../app");
var Study_1 = require("../model/Study");
var Researcher_1 = require("../model/Researcher");
var ResearcherRepository_1 = require("../repository/ResearcherRepository");
var StudyRepository_1 = require("../repository/StudyRepository");
var ParticipantRepository_1 = require("../repository/ParticipantRepository");
var TypeRepository_1 = require("../repository/TypeRepository");
var migrate_1 = require("./migrate");
// FIXME: does not support filtering by Sensor yet.
var SensorEventRepository = /** @class */ (function () {
    function SensorEventRepository() {
    }
    /**
     * Get a set of `SensorEvent`s matching the criteria parameters.
     */
    SensorEventRepository._select = function (
    /**
     *
     */
    id, 
    /**
     *
     */
    sensor_spec, 
    /**
     *
     */
    from_date, 
    /**
     *
     */
    to_date, limit) {
        return __awaiter(this, void 0, void 0, function () {
            var user_id, admin_id, all_res;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        migrate_1._migrate_sensor_event();
                        if (!!id && TypeRepository_1.Identifier_unpack(id)[0] === Researcher_1.Researcher.name)
                            admin_id = ResearcherRepository_1.ResearcherRepository._unpack_id(id).admin_id;
                        else if (!!id && TypeRepository_1.Identifier_unpack(id)[0] === Study_1.Study.name)
                            admin_id = StudyRepository_1.StudyRepository._unpack_id(id).admin_id;
                        else if (!!id && TypeRepository_1.Identifier_unpack(id).length === 0 /* Participant */)
                            user_id = ParticipantRepository_1.ParticipantRepository._unpack_id(id).study_id;
                        else if (!!id)
                            throw new Error("400.invalid-identifier");
                        return [4 /*yield*/, app_1.Database.use("sensor_event").find({
                                selector: {
                                    "#parent": id,
                                    sensor: sensor_spec,
                                    timestamp: from_date === undefined && to_date === undefined
                                        ? undefined
                                        : {
                                            $gte: from_date,
                                            $lt: from_date === to_date ? to_date + 1 : to_date,
                                        },
                                },
                                sort: [
                                    {
                                        timestamp: !!limit && limit < 0 ? "asc" : "desc",
                                    },
                                ],
                                limit: Math.abs((limit !== null && limit !== void 0 ? limit : 1000)),
                            })];
                    case 1:
                        all_res = (_a.sent()).docs.map(function (x) { return (__assign(__assign({}, x), { _id: undefined, _rev: undefined, "#parent": undefined })); });
                        return [2 /*return*/, all_res
                            // Perform a group-by operation on the participant ID if needed.
                            /*return !admin_id
                              ? all_res
                              : all_res.reduce((prev: any, curr: any) => {
                                  let key = (<any>curr).parent
                                  ;(prev[key] ? prev[key] : (prev[key] = null || [])).push({
                                    ...curr,
                                    '#parent': undefined
                                  })
                                  return prev
                                }, <any>{})*/
                        ];
                }
            });
        });
    };
    /**
     * Create a `SensorEvent` with a new object.
     */
    SensorEventRepository._insert = function (
    /**
     * The `StudyId` column of the `Users` table in the LAMP v0.1 DB.
     */
    participant_id, 
    /**
     * The new object.
     */
    objects) {
        return __awaiter(this, void 0, void 0, function () {
            var data, output;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({
                            docs: objects.map(function (x) {
                                var _a;
                                return ({
                                    "#parent": participant_id,
                                    timestamp: Number.parse(x.timestamp),
                                    sensor: String(x.sensor),
                                    data: (_a = x.data, (_a !== null && _a !== void 0 ? _a : {})),
                                });
                            }),
                        })];
                    case 1:
                        data = _a.sent();
                        output = data.filter(function (x) { return !!x.error; });
                        if (output.length > 0)
                            console.error(output);
                        return [2 /*return*/, {}];
                }
            });
        });
    };
    /**
     * Delete a `SensorEvent` row.
     */
    SensorEventRepository._delete = function (
    /**
     * The `StudyId` column of the `Users` table in the LAMP v0.1 DB.
     */
    participant_id, 
    /**
     *
     */
    sensor_spec, 
    /**
     *
     */
    from_date, 
    /**
     *
     */
    to_date) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                throw new Error("503.unimplemented");
            });
        });
    };
    return SensorEventRepository;
}());
exports.SensorEventRepository = SensorEventRepository;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiU2Vuc29yRXZlbnRSZXBvc2l0b3J5LmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vc3JjL3JlcG9zaXRvcnkvU2Vuc29yRXZlbnRSZXBvc2l0b3J5LnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7QUFBQSw4QkFBaUM7QUFDakMsd0NBQXNDO0FBQ3RDLGtEQUFnRDtBQUVoRCwyRUFBeUU7QUFDekUsaUVBQStEO0FBQy9ELDZFQUEyRTtBQUMzRSwrREFBZ0U7QUFDaEUscUNBQWlEO0FBRWpELG1EQUFtRDtBQUVuRDtJQUFBO0lBd0lBLENBQUM7SUF2SUM7O09BRUc7SUFDaUIsNkJBQU8sR0FBM0I7SUFDRTs7T0FFRztJQUNILEVBQVc7SUFFWDs7T0FFRztJQUNILFdBQW9CO0lBRXBCOztPQUVHO0lBQ0gsU0FBa0I7SUFFbEI7O09BRUc7SUFDSCxPQUFnQixFQUVoQixLQUFjOzs7Ozs7d0JBRWQsK0JBQXFCLEVBQUUsQ0FBQTt3QkFLdkIsSUFBSSxDQUFDLENBQUMsRUFBRSxJQUFJLGtDQUFpQixDQUFDLEVBQUUsQ0FBQyxDQUFDLENBQUMsQ0FBQyxLQUFXLHVCQUFXLENBQUMsSUFBSTs0QkFDN0QsUUFBUSxHQUFHLDJDQUFvQixDQUFDLFVBQVUsQ0FBQyxFQUFFLENBQUMsQ0FBQyxRQUFRLENBQUE7NkJBQ3BELElBQUksQ0FBQyxDQUFDLEVBQUUsSUFBSSxrQ0FBaUIsQ0FBQyxFQUFFLENBQUMsQ0FBQyxDQUFDLENBQUMsS0FBVyxhQUFNLENBQUMsSUFBSTs0QkFBRSxRQUFRLEdBQUcsaUNBQWUsQ0FBQyxVQUFVLENBQUMsRUFBRSxDQUFDLENBQUMsUUFBUSxDQUFBOzZCQUM5RyxJQUFJLENBQUMsQ0FBQyxFQUFFLElBQUksa0NBQWlCLENBQUMsRUFBRSxDQUFDLENBQUMsTUFBTSxLQUFLLENBQUMsQ0FBQyxpQkFBaUI7NEJBQ25FLE9BQU8sR0FBRyw2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