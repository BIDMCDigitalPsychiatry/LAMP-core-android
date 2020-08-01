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
var nanoid_1 = require("nanoid");
var Syncronisation_1 = require("./Syncronisation");
var uuid = nanoid_1.customAlphabet("1234567890abcdefghjkmnpqrstvwxyz", 20); // crockford-32
var app_1 = require("../../app");
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
            var db, all_res, error_1;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        // _migrate_activity_event()
                        //Is it required, as we are in local server?
                        // const _lookup_table = await _migrator_export_table() 
                        // Get the correctly scoped identifier to search within.
                        // let user_id: string | undefined
                        // let admin_id: number | undefined
                        // if (!!id && Identifier_unpack(id)[0] === (<any>Researcher).name)
                        //   admin_id = ResearcherRepository._unpack_id(id).admin_id
                        // else if (!!id && Identifier_unpack(id)[0] === (<any>Study).name) admin_id = StudyRepository._unpack_id(id).admin_id
                        // else if (!!id && Identifier_unpack(id).length === 0 /* Participant */)
                        //   user_id = ParticipantRepository._unpack_id(id).study_id
                        // else if (!!id) throw new Error("400.invalid-identifier")
                        //user_id = !!user_id ? Encrypt(user_id) : undefined
                        app_1.PouchDB.plugin(require('pouchdb-find'));
                        db = new app_1.PouchDB('activity_event');
                        _a.label = 1;
                    case 1:
                        _a.trys.push([1, 3, , 4]);
                        return [4 /*yield*/, db.find({
                                selector: {
                                    "#parent": id,
                                    timestamp: from_date === undefined && to_date === undefined ? {} :
                                        {
                                            $gte: from_date,
                                            $lt: from_date === to_date ? to_date + 1 : to_date,
                                        },
                                    activity: activity_id_or_spec === undefined ? undefined :
                                        { $eq: activity_id_or_spec }
                                },
                                fields: ['id', '_rev', 'activity', 'timestamp', 'duration', '#parent', 'static_data', 'temporal_slices'],
                                sort: [
                                    {
                                        timestamp: !!limit && limit < 0 ? "asc" : "desc",
                                    },
                                ],
                                limit: Math.abs((limit !== null && limit !== void 0 ? limit : 1000))
                            })];
                    case 2:
                        all_res = (_a.sent()).docs.map(function (x) { return (__assign(__assign({}, x), { _id: undefined, _rev: undefined, "#parent": undefined })); });
                        return [2 /*return*/, all_res];
                    case 3:
                        error_1 = _a.sent();
                        return [2 /*return*/, []];
                    case 4: return [2 /*return*/];
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
            var db, data, output;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        //_migrate_activity_event()
                        console.log("pouch");
                        // const _lookup_table = await _migrator_lookup_table() // FIXME
                        // const _lookup_migrator_id = (legacyID: string): string => {
                        //   let match = _lookup_table[legacyID]
                        //   if (match === undefined) {
                        //     match = uuid() // 20-char id for non-Participant objects
                        //     _lookup_table[legacyID] = match
                        //     console.log(`inserting migrator link: ${legacyID} => ${match}`)
                        //     Database.use("root").insert({ _id: `_local/${legacyID}`, value: match } as any)
                        //   }
                        //   return match
                        // }
                        app_1.PouchDB.plugin(require('pouchdb-find'));
                        db = new app_1.PouchDB('activity_event');
                        return [4 /*yield*/, db.bulkDocs({
                                docs: objects.map(function (x) {
                                    var _a, _b, _c, _d;
                                    return ({
                                        "#parent": participant_id,
                                        timestamp: (_a = Number.parse(x.timestamp), (_a !== null && _a !== void 0 ? _a : 0)),
                                        duration: (_b = Number.parse(x.duration), (_b !== null && _b !== void 0 ? _b : 0)),
                                        activity: String(x.activity),
                                        static_data: (_c = x.static_data, (_c !== null && _c !== void 0 ? _c : {})),
                                        temporal_slices: (_d = x.temporal_slices, (_d !== null && _d !== void 0 ? _d : [])),
                                    });
                                }),
                            })];
                    case 1:
                        data = _a.sent();
                        output = data.filter(function (x) { return !!x.error; });
                        try {
                            //sync activity_event data to remote
                            Syncronisation_1.sync('activity_event', 'activity_event');
                        }
                        catch (error) {
                            console.log(output.length);
                        }
                        console.log(output.length);
                        if (output.length > 0) {
                            // tslint:disable-next-line:no-console
                            console.log("datasync");
                        }
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiQWN0aXZpdHlFdmVudFJlcG9zaXRvcnkuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi9zcmMvcmVwb3NpdG9yeS9wb3VjaFJlcG9zaXRvcnkvQWN0aXZpdHlFdmVudFJlcG9zaXRvcnkudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQVNBLGlDQUF1QztBQUN2QyxtREFBdUM7QUFDdkMsSUFBTSxJQUFJLEdBQUcsdUJBQWMsQ0FBQyxrQ0FBa0MsRUFBRSxFQUFFLENBQUMsQ0FBQSxDQUFDLGVBQWU7QUFDbkYsaUNBQW1DO0FBRW5DLHlEQUF5RDtBQUV6RDtJQUFBO0lBcUxBLENBQUM7SUFwTEM7O09BRUc7SUFDaUIsK0JBQU8sR0FBM0I7SUFDRTs7T0FFRztJQUNILEVBQVc7SUFFWDs7T0FFRztJQUNILG1CQUE0QjtJQUU1Qjs7T0FFRztJQUNILFNBQWtCO0lBRWxCOztPQUVHO0lBQ0gsT0FBZ0IsRUFFaEIsS0FBYzs7Ozs7O3dCQUVkLDRCQUE0Qjt3QkFFNUIsNENBQTRDO3dCQUM1Qyx3REFBd0Q7d0JBRXhELHdEQUF3RDt3QkFDeEQsa0NBQWtDO3dCQUNsQyxtQ0FBbUM7d0JBQ25DLG1FQUFtRTt3QkFDbkUsNERBQTREO3dCQUM1RCxzSEFBc0g7d0JBQ3RILHlFQUF5RTt3QkFDekUsNERBQTREO3dCQUM1RCwyREFBMkQ7d0JBQzNELG9EQUFvRDt3QkFFcEQsYUFBTyxDQUFDLE1BQU0sQ0FBQyxPQUFPLENBQUMsY0FBYyxDQUFDLENBQUMsQ0FBQzt3QkFDbEMsRUFBRSxHQUFHLElBQUksYUFBTyxDQUFDLGdCQUFnQixDQUFDLENBQUM7Ozs7d0JBSXJCLHFCQUFNLEVBQUUsQ0FBQyxJQUFJLENBQUM7Z0NBQzlCLFFBQVEsRUFBRTtvQ0FDUixTQUFTLEVBQUUsRUFBRTtvQ0FFYixTQUFTLEVBQ1AsU0FBUyxLQUFLLFNBQVMsSUFBSSxPQUFPLEtB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