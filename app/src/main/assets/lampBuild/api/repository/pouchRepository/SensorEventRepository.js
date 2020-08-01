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
var Syncronisation_1 = require("./Syncronisation");
var app_1 = require("../../app");
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
            var db, all_res, error_1;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        // _migrate_sensor_event()
                        app_1.PouchDB.plugin(require('pouchdb-find'));
                        db = new app_1.PouchDB('sensor_event');
                        _a.label = 1;
                    case 1:
                        _a.trys.push([1, 3, , 4]);
                        return [4 /*yield*/, db.find({
                                selector: {
                                    "#parent": id,
                                    sensor: sensor_spec,
                                    timestamp: from_date === undefined && to_date === undefined ? {} :
                                        {
                                            $gte: from_date,
                                            $lt: from_date === to_date ? to_date + 1 : to_date,
                                        },
                                },
                                sort: [
                                    {
                                        timestamp: !!limit && limit < 0 ? "desc" : "asc",
                                    },
                                ],
                                limit: Math.abs((limit !== null && limit !== void 0 ? limit : 1000)),
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
            var db, data, output;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        //_migrate_sensor_event()
                        app_1.PouchDB.plugin(require('pouchdb-find'));
                        db = new app_1.PouchDB('sensor_event');
                        console.log("repopouch");
                        return [4 /*yield*/, db.bulkDocs({
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
                        try {
                            //sync sensor_event data to remote
                            Syncronisation_1.sync('sensor_event', 'sensor_event');
                        }
                        catch (error) {
                            console.log(output.length);
                        }
                        if (output.length > 0) {
                        }
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiU2Vuc29yRXZlbnRSZXBvc2l0b3J5LmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vc3JjL3JlcG9zaXRvcnkvcG91Y2hSZXBvc2l0b3J5L1NlbnNvckV2ZW50UmVwb3NpdG9yeS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7O0FBU0EsbURBQXVDO0FBQ3ZDLGlDQUFtQztBQUNuQyxtREFBbUQ7QUFFbkQ7SUFBQTtJQXlKQSxDQUFDO0lBeEpDOztPQUVHO0lBQ2lCLDZCQUFPLEdBQTNCO0lBQ0U7O09BRUc7SUFDSCxFQUFXO0lBRVg7O09BRUc7SUFDSCxXQUFvQjtJQUVwQjs7T0FFRztJQUNILFNBQWtCO0lBRWxCOztPQUVHO0lBQ0gsT0FBZ0IsRUFFaEIsS0FBYzs7Ozs7O3dCQUVkLDBCQUEwQjt3QkFDMUIsYUFBTyxDQUFDLE1BQU0sQ0FBQyxPQUFPLENBQUMsY0FBYyxDQUFDLENBQUMsQ0FBQzt3QkFDbEMsRUFBRSxHQUFHLElBQUksYUFBTyxDQUFDLGNBQWMsQ0FBQyxDQUFDOzs7O3dCQWNyQyxxQkFBTSxFQUFFLENBQUMsSUFBSSxDQUFDO2dDQUNaLFFBQVEsRUFBRTtvQ0FDUixTQUFTLEVBQUUsRUFBRztvQ0FDZCxNQUFNLEVBQUUsV0FBVztvQ0FDbkIsU0FBUyxFQUNULFNBQVMsS0FBSyxTQUFTLElBQU0sT0FBTyxLQUFLLFNBQVMsQ0FBQyxDQUFDLENBQUMsRUFBRSxDQUFFLENBQUM7d0NBQ3JEOzRDQUNHLElBQUksRUFBRSxTQUFTOzRDQUNmLEdBQUcsRUFBRSxTQUFTLEtBQUssT0FBTyxDQUFDLENBQUMsQ0FBQyxPQUFRLEdBQUcsQ0FBQyxDQUFDLENBQUMsQ0FBQyxPQUFPO3lDQUNwRDtpQ0FDUjtnQ0FDRCxJQUFJLEVBQUU7b0NBQ0o7d0NBQ0UsU0FBUyxFQUFFLENBQUMsQ0FBQyxLQUFLLElBQUksS0FBSyxHQUFHLENBQUMsQ0FB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