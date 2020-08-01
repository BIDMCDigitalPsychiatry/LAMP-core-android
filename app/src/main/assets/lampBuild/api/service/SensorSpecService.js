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
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
var express_1 = require("express");
var SensorSpecRepository_1 = require("../repository/SensorSpecRepository");
var Security_1 = require("./Security");
var SensorSpecRepository_2 = require("../repository/pouchRepository/SensorSpecRepository");
var jsonata_1 = __importDefault(require("jsonata"));
exports.SensorSpecService = express_1.Router();
exports.SensorSpecService.post("/sensor_spec", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var sensor_spec, _, output, _a, e_1;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                sensor_spec = req.body;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"])];
            case 1:
                _ = _b.sent();
                _a = {};
                return [4 /*yield*/, SensorSpecRepository_1.SensorSpecRepository._insert(sensor_spec)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_1 = _b.sent();
                if (e_1.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_1.message.split(".")[0]) || 500).json({ error: e_1.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.SensorSpecService.put("/sensor_spec/:sensor_spec_name", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var sensor_spec_name, sensor_spec, _, output, _a, e_2;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                sensor_spec_name = req.params.sensor_spec_name;
                sensor_spec = req.body;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"])];
            case 1:
                _ = _b.sent();
                _a = {};
                return [4 /*yield*/, SensorSpecRepository_1.SensorSpecRepository._update(sensor_spec_name, sensor_spec)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_2 = _b.sent();
                if (e_2.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_2.message.split(".")[0]) || 500).json({ error: e_2.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.SensorSpecService.delete("/sensor_spec/:sensor_spec_name", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var sensor_spec_name, _, output, _a, e_3;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                sensor_spec_name = req.params.sensor_spec_name;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"])];
            case 1:
                _ = _b.sent();
                _a = {};
                return [4 /*yield*/, SensorSpecRepository_1.SensorSpecRepository._delete(sensor_spec_name)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_3 = _b.sent();
                if (e_3.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_3.message.split(".")[0]) || 500).json({ error: e_3.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.SensorSpecService.get("/sensor_spec/:sensor_spec_name", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var sensor_spec_name, outputLocal, _a, _, output, _b, e_4;
    return __generator(this, function (_c) {
        switch (_c.label) {
            case 0:
                _c.trys.push([0, 6, , 7]);
                sensor_spec_name = req.params.sensor_spec_name;
                if (!(process.env.LOCAL_DATA === "true")) return [3 /*break*/, 2];
                _a = {};
                return [4 /*yield*/, SensorSpecRepository_2.SensorSpecRepository._select(sensor_spec_name)];
            case 1:
                outputLocal = (_a.data = _c.sent(), _a);
                outputLocal = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(outputLocal) : outputLocal;
                res.json(outputLocal);
                return [3 /*break*/, 5];
            case 2: return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"])];
            case 3:
                _ = _c.sent();
                _b = {};
                return [4 /*yield*/, SensorSpecRepository_1.SensorSpecRepository._select(sensor_spec_name)];
            case 4:
                output = (_b.data = _c.sent(), _b);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                _c.label = 5;
            case 5: return [3 /*break*/, 7];
            case 6:
                e_4 = _c.sent();
                if (e_4.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_4.message.split(".")[0]) || 500).json({ error: e_4.message });
                return [3 /*break*/, 7];
            case 7: return [2 /*return*/];
        }
    });
}); });
exports.SensorSpecService.get("/sensor_spec", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var outputLocal, _a, _, output, _b, e_5;
    return __generator(this, function (_c) {
        switch (_c.label) {
            case 0:
                _c.trys.push([0, 6, , 7]);
                if (!(process.env.LOCAL_DATA === "true")) return [3 /*break*/, 2];
                _a = {};
                return [4 /*yield*/, SensorSpecRepository_2.SensorSpecRepository._select()];
            case 1:
                outputLocal = (_a.data = _c.sent(), _a);
                outputLocal = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(outputLocal) : outputLocal;
                res.json(outputLocal);
                return [3 /*break*/, 5];
            case 2: return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"])];
            case 3:
                _ = _c.sent();
                _b = {};
                return [4 /*yield*/, SensorSpecRepository_1.SensorSpecRepository._select()];
            case 4:
                output = (_b.data = _c.sent(), _b);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                _c.label = 5;
            case 5: return [3 /*break*/, 7];
            case 6:
                e_5 = _c.sent();
                if (e_5.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_5.message.split(".")[0]) || 500).json({ error: e_5.message });
                return [3 /*break*/, 7];
            case 7: return [2 /*return*/];
        }
    });
}); });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiU2Vuc29yU3BlY1NlcnZpY2UuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi9zcmMvc2VydmljZS9TZW5zb3JTcGVjU2VydmljZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUFBLG1DQUFtRDtBQUVuRCwyRUFBeUU7QUFDekUsdUNBQW9FO0FBQ3BFLDJGQUFnSDtBQUNoSCxvREFBNkI7QUFFaEIsUUFBQSxpQkFBaUIsR0FBRyxnQkFBTSxFQUFFLENBQUE7QUFDekMseUJBQWlCLENBQUMsSUFBSSxDQUFDLGNBQWMsRUFBRSxVQUFPLEdBQVksRUFBRSxHQUFhOzs7Ozs7Z0JBRS9ELFdBQVcsR0FBRyxHQUFHLENBQUMsSUFBSSxDQUFBO2dCQUNsQixxQkFBTSxrQkFBTyxDQUFDLEdBQUcsQ0FBQyxHQUFHLENBQUMsZUFBZSxDQUFDLEVBQUUsQ0FBQyxNQUFNLEVBQUUsU0FBUyxFQUFFLFFBQVEsQ0FBQyxDQUFDLEVBQUE7O2dCQUExRSxDQUFDLEdBQUcsU0FBc0U7O2dCQUN6RCxxQkFBTSwyQ0FBb0IsQ0FBQyxPQUFPLENBQUMsV0FBVyxDQUFDLEVBQUE7O2dCQUFoRSxNQUFNLElBQUssT0FBSSxHQUFFLFNBQStDLEtBQUU7Z0JBQ3hFLEdBQUcsQ0FBQyxJQUFJLENBQUMsTUFBTSxDQUFDLENBQUE7Ozs7Z0JBRWhCLElBQUksR0FBQyxDQUFDLE9BQU8sS0FBSyx5QkFBeUI7b0JBQUUsR0FBRyxDQUFDLEdBQUcsQ0FBQyxrQkFBa0IsRUFBRSx3Q0FBb0MsQ0FBQyxDQUFBO2dCQUM5RyxHQUFHLENBQUMsTUFBTSxDQUFDLFFBQVEsQ0FBQyxHQUFDLENBQUMsT0FBTyxDQUFDLEtBQUssQ0FBQyxHQUFHLENBQUMsQ0FBQyxDQUFDLENBQUMsQ0FBQyxJQUFJLEdBQUcsQ0FBQyxDQUFDLElBQUksQ0FBQyxFQUFFLEtBQUssRUFBRSxHQUFDLENBQUMsT0FBTyxFQUFFLENB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