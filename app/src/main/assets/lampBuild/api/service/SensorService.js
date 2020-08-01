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
var SensorRepository_1 = require("../repository/SensorRepository");
var Security_1 = require("./Security");
var jsonata_1 = __importDefault(require("jsonata"));
exports.SensorService = express_1.Router();
exports.SensorService.post("/study/:study_id/sensor", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var study_id, sensor, output, _a, e_1;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                study_id = req.params.study_id;
                sensor = req.body;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], study_id)];
            case 1:
                study_id = _b.sent();
                _a = {};
                return [4 /*yield*/, SensorRepository_1.SensorRepository._insert(study_id, sensor)];
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
exports.SensorService.put("/sensor/:sensor_id", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var sensor_id, sensor, output, _a, e_2;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                sensor_id = req.params.sensor_id;
                sensor = req.body;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], sensor_id)];
            case 1:
                sensor_id = _b.sent();
                _a = {};
                return [4 /*yield*/, SensorRepository_1.SensorRepository._update(sensor_id, sensor)];
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
exports.SensorService.delete("/sensor/:sensor_id", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var sensor_id, output, _a, e_3;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                sensor_id = req.params.sensor_id;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], sensor_id)];
            case 1:
                sensor_id = _b.sent();
                _a = {};
                return [4 /*yield*/, SensorRepository_1.SensorRepository._delete(sensor_id)];
            case 2:
                output = (_a.data = _b.sent(), _a);
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
exports.SensorService.get("/sensor/:sensor_id", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var sensor_id, outputLocal, _a, output, _b, e_4;
    return __generator(this, function (_c) {
        switch (_c.label) {
            case 0:
                _c.trys.push([0, 6, , 7]);
                sensor_id = req.params.sensor_id;
                if (!(process.env.LOCAL_DATA === "true")) return [3 /*break*/, 2];
                _a = {};
                return [4 /*yield*/, SensorRepository_1.SensorRepository._select(sensor_id)];
            case 1:
                outputLocal = (_a.data = _c.sent(), _a);
                outputLocal = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(outputLocal) : outputLocal;
                res.json(outputLocal);
                return [3 /*break*/, 5];
            case 2: return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], sensor_id)];
            case 3:
                sensor_id = _c.sent();
                _b = {};
                return [4 /*yield*/, SensorRepository_1.SensorRepository._select(sensor_id)];
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
exports.SensorService.get("/participant/:participant_id/sensor", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var participant_id, output, _a, e_5;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                participant_id = req.params.participant_id;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], participant_id)];
            case 1:
                participant_id = _b.sent();
                _a = {};
                return [4 /*yield*/, SensorRepository_1.SensorRepository._select(participant_id)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_5 = _b.sent();
                if (e_5.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_5.message.split(".")[0]) || 500).json({ error: e_5.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.SensorService.get("/study/:study_id/sensor", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var study_id, output, _a, e_6;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                study_id = req.params.study_id;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], study_id)];
            case 1:
                study_id = _b.sent();
                _a = {};
                return [4 /*yield*/, SensorRepository_1.SensorRepository._select(study_id)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_6 = _b.sent();
                if (e_6.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_6.message.split(".")[0]) || 500).json({ error: e_6.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.SensorService.get("/researcher/:researcher_id/sensor", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var researcher_id, output, _a, e_7;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                researcher_id = req.params.researcher_id;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], researcher_id)];
            case 1:
                researcher_id = _b.sent();
                _a = {};
                return [4 /*yield*/, SensorRepository_1.SensorRepository._select(researcher_id)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_7 = _b.sent();
                if (e_7.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_7.message.split(".")[0]) || 500).json({ error: e_7.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.SensorService.get("/sensor", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var output, _a, _, output, _b, e_8;
    return __generator(this, function (_c) {
        switch (_c.label) {
            case 0:
                _c.trys.push([0, 6, , 7]);
                if (!(process.env.LOCAL_DATA === "true")) return [3 /*break*/, 2];
                _a = {};
                return [4 /*yield*/, SensorRepository_1.SensorRepository._select()];
            case 1:
                output = (_a.data = _c.sent(), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                return [3 /*break*/, 5];
            case 2: return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["parent"])];
            case 3:
                _ = _c.sent();
                _b = {};
                return [4 /*yield*/, SensorRepository_1.SensorRepository._select()];
            case 4:
                output = (_b.data = _c.sent(), _b);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                _c.label = 5;
            case 5: return [3 /*break*/, 7];
            case 6:
                e_8 = _c.sent();
                if (e_8.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_8.message.split(".")[0]) || 500).json({ error: e_8.message });
                return [3 /*break*/, 7];
            case 7: return [2 /*return*/];
        }
    });
}); });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiU2Vuc29yU2VydmljZS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uL3NyYy9zZXJ2aWNlL1NlbnNvclNlcnZpY2UudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7QUFBQSxtQ0FBbUQ7QUFFbkQsbUVBQWlFO0FBQ2pFLHVDQUFvRTtBQUNwRSxvREFBNkI7QUFFaEIsUUFBQSxhQUFhLEdBQUcsZ0JBQU0sRUFBRSxDQUFBO0FBQ3JDLHFCQUFhLENBQUMsSUFBSSxDQUFDLHlCQUF5QixFQUFFLFVBQU8sR0FBWSxFQUFFLEdBQWE7Ozs7OztnQkFFeEUsUUFBUSxHQUFHLEdBQUcsQ0FBQyxNQUFNLENBQUMsUUFBUSxDQUFBO2dCQUM1QixNQUFNLEdBQUcsR0FBRyxDQUFDLElBQUksQ0FBQTtnQkFDWixxQkFBTSxrQkFBTyxDQUFDLEdBQUcsQ0FBQyxHQUFHLENBQUMsZUFBZSxDQUFDLEVBQUUsQ0FBQyxNQUFNLEVBQUUsU0FBUyxFQUFFLFFBQVEsQ0FBQyxFQUFFLFFBQVEsQ0FBQyxFQUFBOztnQkFBM0YsUUFBUSxHQUFHLFNBQWdGLENBQUE7O2dCQUNwRSxxQkFBTSxtQ0FBZ0IsQ0FBQyxPQUFPLENBQUMsUUFBUSxFQUFFLE1BQU0sQ0FBQyxFQUFBOztnQkFBakUsTUFBTSxJQUFLLE9BQUksR0FBRSxTQUFnRCxLQUFFO2dCQUN6RSxHQUFHLENBQUMsSUFBSSxDQUFDLE1BQU0sQ0FBQyxDQUFBOzs7O2dCQUVoQixJQUFJLEdBQUMsQ0FBQyxPQUFPLEtBQUsseUJBQXlCO29CQUFFLEdBQUcsQ0FBQyxHQUFHLENBQUMsa0JBQWtCLEVBQUUsd0NBQW9DLENBQUMsQ0FBQTtnQkFDOUcsR0FBRyxDQUFDLE1BQU0sQ0FBQyxRQUFRLENBQUMsR0FBQyxDQUFDLE9BQU8sQ0FBQyxLQUFLLENBQUMsR0FBRyxDQUFDLENBQUMsQ0FBQyxDQUFDLENBQUMsSUFBSSxHQUFHLENBQUMsQ0FB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