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
var SensorRepository = /** @class */ (function () {
    function SensorRepository() {
    }
    /**
     * Get a set of `Sensor`s matching the criteria parameters.
     */
    SensorRepository._select = function (
    /**
     * The identifier of the object or any parent.
     */
    id) {
        return __awaiter(this, void 0, void 0, function () {
            var data;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, app_1.Database.use("sensor").list({ include_docs: true, start_key: id, end_key: id })];
                    case 1:
                        data = _a.sent();
                        return [2 /*return*/, data.rows.map(function (x) { return (__assign(__assign({ id: x.doc._id }, x.doc), { _id: undefined, _rev: undefined })); })];
                }
            });
        });
    };
    /**
     * Create a `Sensor` with a new object.
     */
    SensorRepository._insert = function (id, 
    /**
     * The new object.
     */
    object) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                // TODO: Sensors do not exist! They cannot be modified!
                throw new Error("503.unimplemented");
            });
        });
    };
    /**
     * Update a `Sensor` with new fields.
     */
    SensorRepository._update = function (
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
                // TODO: Sensors do not exist! They cannot be modified!
                throw new Error("503.unimplemented");
            });
        });
    };
    /**
     * Delete a `Sensor` row.
     */
    SensorRepository._delete = function (
    /**
     *
     */
    sensor_spec_name) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                // TODO: Sensors do not exist! They cannot be modified!
                throw new Error("503.unimplemented");
            });
        });
    };
    return SensorRepository;
}());
exports.SensorRepository = SensorRepository;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiU2Vuc29yUmVwb3NpdG9yeS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uL3NyYy9yZXBvc2l0b3J5L1NlbnNvclJlcG9zaXRvcnkudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUFBLDhCQUFpQztBQUdqQztJQUFBO0lBa0VBLENBQUM7SUFqRUM7O09BRUc7SUFDaUIsd0JBQU8sR0FBM0I7SUFDRTs7T0FFRztJQUNILEVBQVc7Ozs7OzRCQUVFLHFCQUFNLGNBQVEsQ0FBQyxHQUFHLENBQUMsUUFBUSxDQUFDLENBQUMsSUFBSSxDQUFDLEVBQUUsWUFBWSxFQUFFLElBQUksRUFBRSxTQUFTLEVBQUUsRUFBRSxFQUFFLE9BQU8sRUFBRSxFQUFFLEVBQUUsQ0FBQyxFQUFBOzt3QkFBNUYsSUFBSSxHQUFHLFNBQXFGO3dCQUNsRyxzQkFBUSxJQUFJLENBQUMsSUFBWSxDQUFDLEdBQUcsQ0FBQyxVQUFDLENBQU0sSUFBSyxPQUFBLHFCQUN4QyxFQUFFLEVBQUUsQ0FBQyxDQUFDLEdBQUcsQ0FBQyxHQUFHLElBQ1YsQ0FBQyxDQUFDLEdBQUcsS0FDUixHQUFHLEVBQUUsU0FBUyxFQUNkLElBQUksRUFBRSxTQUFTLElBQ2YsRUFMd0MsQ0FLeEMsQ0FBQyxFQUFBOzs7O0tBQ0o7SUFFRDs7T0FFRztJQUNpQix3QkFBTyxHQUEzQixVQUNFLEVBQVU7SUFDVjs7T0FFRztJQUNILE1BQWM7OztnQkFFZCx1REFBdUQ7Z0JBQ3ZELE1BQU0sSUFBSSxLQUFLLENBQUMsbUJBQW1CLENBQUMsQ0FBQTs7O0tBRXJDO0lBRUQ7O09BRUc7SUFDaUIsd0JBQU8sR0FBM0I7SUFDRTs7T0FFRztJQUNILGdCQUF3QjtJQUV4Qjs7T0FFRztJQUNILE1BQWM7OztnQkFFZCx1REFBdUQ7Z0JBQ3ZELE1BQU0sSUFBSSxLQUFLLENBQUMsbUJBQW1CLENBQUMsQ0FBQTs7O0tBRXJDO0lBRUQ7O09BRUc7SUFDaUIsd0JBQU8sR0FBM0I7SUFDRTs7T0FFRztJQUNILGdCQUF3Qjs7O2dCQUV4Qix1REFBdUQ7Z0JBQ3ZELE1BQU0sSUFBSSxLQUFLLENBQUMsbUJBQW1CLENBQUMsQ0FBQTs7O0tBRXJDO0lBQ0gsdUJBQUM7QUFBRCxDQUFDLEFBbEVELElBa0VDO0FBbEVZLDRDQUFnQiJ9