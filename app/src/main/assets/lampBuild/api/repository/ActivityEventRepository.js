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
var nanoid_1 = require("nanoid");
var uuid = nanoid_1.customAlphabet("1234567890abcdefghjkmnpqrstvwxyz", 20); // crockford-32
// FIXME: does not support filtering by ActivitySpec yet.
var ActivityEventRepository = /** @class */ (function () {
    function ActivityEventRepository() {
    }
    /**
     * Get a set of `ActivityEvent`s matching the criteria parameters.
     */
    ActivityEventRepository._select = function (
    /**
     *
     */
    id, 
    /**
     *
     */
    activity_id_or_spec, 
    /**
     *
     */
    from_date, 
    /**
     *
     */
    to_date, limit) {
        return __awaiter(this, void 0, void 0, function () {
            var _lookup_table, user_id, admin_id, all_res;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        migrate_1._migrate_activity_event();
                        return [4 /*yield*/, migrate_1._migrator_export_table()
                            // Get the correctly scoped identifier to search within.
                        ];
                    case 1:
                        _lookup_table = _a.sent();
                        if (!!id && TypeRepository_1.Identifier_unpack(id)[0] === Researcher_1.Researcher.name)
                            admin_id = ResearcherRepository_1.ResearcherRepository._unpack_id(id).admin_id;
                        else if (!!id && TypeRepository_1.Identifier_unpack(id)[0] === Study_1.Study.name)
                            admin_id = StudyRepository_1.StudyRepository._unpack_id(id).admin_id;
                        else if (!!id && TypeRepository_1.Identifier_unpack(id).length === 0 /* Participant */)
                            user_id = ParticipantRepository_1.ParticipantRepository._unpack_id(id).study_id;
                        else if (!!id)
                            throw new Error("400.invalid-identifier");
                        return [4 /*yield*/, app_1.Database.use("activity_event").find({
                                selector: {
                                    "#parent": id,
                                    activity: activity_id_or_spec,
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
                    case 2:
                        all_res = (_a.sent()).docs.map(function (x) { return (__assign(__assign({}, x), { _id: undefined, _rev: undefined, "#parent": undefined, activity: _lookup_table[x.activity] })); });
                        return [2 /*return*/, all_res
                            // Perform a group-by operation on the participant ID if needed.
                            /*return !admin_id
                              ? all_res
                              : all_res.reduce((prev, curr: any) => {
                                  const key = (<any>curr).parent
                                  ;(prev[key] ? prev[key] : (prev[key] = null || [])).push({ ...curr, parent: undefined })
                                  return prev
                                }, <any>{})
                            */
                        ];
                }
            });
        });
    };
    /**
     * Add a new `ActivityEvent` with new fields.
     */
    ActivityEventRepository._insert = function (
    /**
     * The `StudyId` column of the `Users` table in the LAMP v0.1 DB.
     */
    participant_id, 
    /**
     * The new object to append.
     */
    objects) {
        return __awaiter(this, void 0, void 0, function () {
            var _lookup_table, _lookup_migrator_id, data, output;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, migrate_1._migrator_lookup_table()]; // FIXME
                    case 1:
                        _lookup_table = _a.sent() // FIXME
                        ;
                        _lookup_migrator_id = function (legacyID) {
                            var match = _lookup_table[legacyID];
                            if (match === undefined) {
                                match = uuid(); // 20-char id for non-Participant objects
                                _lookup_table[legacyID] = match;
                                console.log("inserting migrator link: " + legacyID + " => " + match);
                                app_1.Database.use("root").insert({ _id: "_local/" + legacyID, value: match });
                            }
                            return match;
                        };
                        return [4 /*yield*/, app_1.Database.use("activity_event").bulk({
                                docs: objects.map(function (x) {
                                    var _a, _b, _c, _d;
                                    return ({
                                        "#parent": participant_id,
                                        timestamp: (_a = Number.parse(x.timestamp), (_a !== null && _a !== void 0 ? _a : 0)),
                                        duration: (_b = Number.parse(x.duration), (_b !== null && _b !== void 0 ? _b : 0)),
                                        activity: _lookup_migrator_id(String(x.activity)),
                                        static_data: (_c = x.static_data, (_c !== null && _c !== void 0 ? _c : {})),
                                        temporal_slices: (_d = x.temporal_slices, (_d !== null && _d !== void 0 ? _d : [])),
                                    });
                                }),
                            })];
                    case 2:
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
     * Deletes a `ActivityEvent` row.
     */
    ActivityEventRepository._delete = function (
    /**
     * The `StudyId` column of the `Users` table in the LAMP v0.1 DB.
     */
    participant_id, 
    /**
     *
     */
    activity_id_or_spec, 
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
    return ActivityEventRepository;
}());
exports.ActivityEventRepository = ActivityEventRepository;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiQWN0aXZpdHlFdmVudFJlcG9zaXRvcnkuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi9zcmMvcmVwb3NpdG9yeS9BY3Rpdml0eUV2ZW50UmVwb3NpdG9yeS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7O0FBQUEsOEJBQWlDO0FBQ2pDLHdDQUFzQztBQUN0QyxrREFBZ0Q7QUFFaEQsMkVBQXlFO0FBQ3pFLGlFQUErRDtBQUMvRCw2RUFBMkU7QUFDM0UsK0RBQWdFO0FBQ2hFLHFDQUFtRztBQUNuRyxpQ0FBdUM7QUFDdkMsSUFBTSxJQUFJLEdBQUcsdUJBQWMsQ0FBQyxrQ0FBa0MsRUFBRSxFQUFFLENBQUMsQ0FBQSxDQUFDLGVBQWU7QUFFbkYseURBQXlEO0FBRXpEO0lBQUE7SUFzSkEsQ0FBQztJQXJKQzs7T0FFRztJQUNpQiwrQkFBTyxHQUEzQjtJQUNFOztPQUVHO0lBQ0gsRUFBVztJQUVYOztPQUVHO0lBQ0gsbUJBQTRCO0lBRTVCOztPQUVHO0lBQ0gsU0FBa0I7SUFFbEI7O09BRUc7SUFDSCxPQUFnQixFQUVoQixLQUFjOzs7Ozs7d0JBRWQsaUNBQXVCLEVBQUUsQ0FBQTt3QkFDSCxxQkFBTSxnQ0FBc0IsRUFBRTs0QkFFcEQsd0RBQXdEOzBCQUZKOzt3QkFBOUMsYUFBYSxHQUFHLFNBQThCO3dCQUtwRCxJQUFJLENBQUMsQ0FBQyxFQUFFLElBQUksa0NBQWlCLENBQUMsRUFBRSxDQUFDLENBQUMsQ0FBQyxDQUFDLEtBQVcsdUJBQVcsQ0FBQyxJQUFJOzRCQUM3RCxRQUFRLEdBQUcsMkNBQW9CLENBQUMsVUFBVSxDQUFDLEVBQUUsQ0FBQyxDQUFDLFFBQVEsQ0FBQTs2QkFDcEQsSUFBSSxDQUFDLENBQUMsRUFBRSxJQUFJLGtDQUFpQixDQUFDLEVBQUUsQ0FBQyxDQUFDLENBQUMsQ0FBQyxLQUFXLGFBQU0sQ0FBQyxJQUFJOzRCQUFFLFFBQVEs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