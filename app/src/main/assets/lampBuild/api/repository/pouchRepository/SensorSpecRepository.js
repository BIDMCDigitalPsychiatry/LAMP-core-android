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
var app_1 = require("../../app");
var SensorSpecRepository = /** @class */ (function () {
    function SensorSpecRepository() {
    }
    /**
     * Get a set of `SensorSpec`s matching the criteria parameters.
     */
    SensorSpecRepository._select = function (
    /**
     * The identifier of the object or any parent.
     */
    id) {
        return __awaiter(this, void 0, void 0, function () {
            var db, data;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        console.log("SensorSPECPouch");
                        app_1.PouchDB.plugin(require('pouchdb-find'));
                        db = new app_1.PouchDB('sensor_spec');
                        return [4 /*yield*/, db.allDocs({ include_docs: true, start_key: id, end_key: id })];
                    case 1:
                        data = _a.sent();
                        return [2 /*return*/, data.rows.map(function (x) { return (__assign(__assign({ id: x.doc._id }, x.doc), { _id: undefined, _rev: undefined })); })];
                }
            });
        });
    };
    /**
     * Create a `SensorSpec` with a new object.
     */
    SensorSpecRepository._insert = function (
    /**
     * The new object.
     */
    object) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                // TODO: SensorSpecs do not exist! They cannot be modified!
                throw new Error("503.unimplemented");
            });
        });
    };
    /**
     * Update a `SensorSpec` with new fields.
     */
    SensorSpecRepository._update = function (
    /**
     *
     */
    sensor_spec_name, 
    /**
     * The replacement object or specific fields within.
     */
    object) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                // TODO: SensorSpecs do not exist! They cannot be modified!
                throw new Error("503.unimplemented");
            });
        });
    };
    /**
     * Delete a `SensorSpec` row.
     */
    SensorSpecRepository._delete = function (
    /**
     *
     */
    sensor_spec_name) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                // TODO: SensorSpecs do not exist! They cannot be modified!
                throw new Error("503.unimplemented");
            });
        });
    };
    return SensorSpecRepository;
}());
exports.SensorSpecRepository = SensorSpecRepository;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiU2Vuc29yU3BlY1JlcG9zaXRvcnkuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi9zcmMvcmVwb3NpdG9yeS9wb3VjaFJlcG9zaXRvcnkvU2Vuc29yU3BlY1JlcG9zaXRvcnkudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUVBLGlDQUFtQztBQUNuQztJQUFBO0lBb0VBLENBQUM7SUFuRUM7O09BRUc7SUFDaUIsNEJBQU8sR0FBM0I7SUFDRTs7T0FFRztJQUNILEVBQVc7Ozs7Ozt3QkFFWCxPQUFPLENBQUMsR0FBRyxDQUFDLGlCQUFpQixDQUFDLENBQUM7d0JBQy9CLGFBQU8sQ0FBQyxNQUFNLENBQUMsT0FBTyxDQUFDLGNBQWMsQ0FBQyxDQUFDLENBQUM7d0JBQ2xDLEVBQUUsR0FBRyxJQUFJLGFBQU8sQ0FBQyxhQUFhLENBQUMsQ0FBQzt3QkFDekIscUJBQU0sRUFBRSxDQUFDLE9BQU8sQ0FBQyxFQUFFLFlBQVksRUFBRSxJQUFJLEVBQUUsU0FBUyxFQUFFLEVBQUUsRUFBRSxPQUFPLEVBQUUsRUFBRSxFQUFFLENBQUMsRUFBQTs7d0JBQTNFLElBQUksR0FBRyxTQUFvRTt3QkFDakYsc0JBQVEsSUFBSSxDQUFDLElBQVksQ0FBQyxHQUFHLENBQUMsVUFBQyxDQUFNLElBQUssT0FBQSxxQkFDeEMsRUFBRSxFQUFFLENBQUMsQ0FBQyxHQUFHLENBQUMsR0FBRyxJQUNWLENBQUMsQ0FBQyxHQUFHLEtBQ1IsR0FBRyxFQUFFLFNBQVMsRUFDZCxJQUFJLEVBQUUsU0FBUyxJQUNmLEVBTHdDLENBS3hDLENBQUMsRUFBQTs7OztLQUNKO0lBRUQ7O09BRUc7SUFDaUIsNEJBQU8sR0FBM0I7SUFDRTs7T0FFRztJQUNILE1BQWtCOzs7Z0JBRWxCLDJEQUEyRDtnQkFDM0QsTUFBTSxJQUFJLEtBQUssQ0FBQyxtQkFBbUIsQ0FBQyxDQUFBOzs7S0FFckM7SUFFRDs7T0FFRztJQUNpQiw0QkFBTyxHQUEzQjtJQUNFOztPQUVHO0lBQ0gsZ0JBQXdCO0lBRXhCOztPQUVHO0lBQ0gsTUFBa0I7OztnQkFFbEIsMkRBQTJEO2dCQUMzRCxNQUFNLElBQUksS0FBSyxDQUFDLG1CQUFtQixDQUFDLENBQUE7OztLQUVyQztJQUVEOztPQUVHO0lBQ2lCLDRCQUFPLEdBQTNCO0lBQ0U7O09BRUc7SUFDSCxnQkFBd0I7OztnQkFFeEIsMkRBQTJEO2dCQUMzRCxNQUFNLElBQUksS0FBSyxDQUFDLG1CQUFtQixDQUFDLENBQUE7OztLQUVyQztJQUNILDJCQUFDO0FBQUQsQ0FBQyxBQXBFRCxJQW9FQztBQXBFWSxvREFBb0IifQ==