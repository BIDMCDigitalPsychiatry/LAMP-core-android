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
var SensorEventRepository_1 = require("../repository/SensorEventRepository");
var Security_1 = require("./Security");
var SensorEventRepository_2 = require("../repository/pouchRepository/SensorEventRepository");
var jsonata_1 = __importDefault(require("jsonata"));
exports.SensorEventService = express_1.Router();
exports.SensorEventService.post("/participant/:participant_id/sensor_event", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var participant_id, sensor_event, outputLocal, _a, output, _b, e_1;
    return __generator(this, function (_c) {
        switch (_c.label) {
            case 0:
                _c.trys.push([0, 6, , 7]);
                participant_id = req.params.participant_id;
                sensor_event = req.body;
                if (!(process.env.LOCAL_DATA === "true")) return [3 /*break*/, 2];
                _a = {};
                return [4 /*yield*/, SensorEventRepository_2.SensorEventRepository._insert(participant_id, Array.isArray(sensor_event) ? sensor_event : [sensor_event])];
            case 1:
                outputLocal = (_a.data = _c.sent(),
                    _a);
                res.json(outputLocal);
                return [3 /*break*/, 5];
            case 2: return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], participant_id)];
            case 3:
                participant_id = _c.sent();
                _b = {};
                return [4 /*yield*/, SensorEventRepository_1.SensorEventRepository._insert(participant_id, Array.isArray(sensor_event) ? sensor_event : [sensor_event])];
            case 4:
                output = (_b.data = _c.sent(),
                    _b);
                res.json(output);
                _c.label = 5;
            case 5: return [3 /*break*/, 7];
            case 6:
                e_1 = _c.sent();
                if (e_1.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_1.message.split(".")[0]) || 500).json({ error: e_1.message });
                return [3 /*break*/, 7];
            case 7: return [2 /*return*/];
        }
    });
}); });
exports.SensorEventService.delete("/participant/:participant_id/sensor_event", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var participant_id, origin, from, to, output, _a, e_2;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                participant_id = req.params.participant_id;
                origin = req.query.origin;
                from = Number.parse(req.query.from);
                to = Number.parse(req.query.to);
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], participant_id)];
            case 1:
                participant_id = _b.sent();
                _a = {};
                return [4 /*yield*/, SensorEventRepository_1.SensorEventRepository._delete(participant_id, origin, from, to)];
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
exports.SensorEventService.get("/participant/:participant_id/sensor_event", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var participant_id, origin, from, to, limit, outputLocal, _a, output, _b, e_3;
    var _c;
    return __generator(this, function (_d) {
        switch (_d.label) {
            case 0:
                _d.trys.push([0, 6, , 7]);
                participant_id = req.params.participant_id;
                origin = req.query.origin;
                from = Number.parse(req.query.from);
                to = Number.parse(req.query.to);
                limit = Math.min(Math.max((_c = Number.parse(req.query.limit), (_c !== null && _c !== void 0 ? _c : 1000)), -1000), 1000) // clamped to [-1000, 1000]
                ;
                if (!(process.env.LOCAL_DATA === "true")) return [3 /*break*/, 2];
                console.log("poucservice");
                _a = {};
                return [4 /*yield*/, SensorEventRepository_2.SensorEventRepository._select(participant_id, origin, from, to, limit)];
            case 1:
                outputLocal = (_a.data = _d.sent(), _a);
                outputLocal = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(outputLocal) : outputLocal;
                res.json(outputLocal);
                return [3 /*break*/, 5];
            case 2:
                console.log("poucservice22");
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], participant_id)];
            case 3:
                participant_id = _d.sent();
                _b = {};
                return [4 /*yield*/, SensorEventRepository_1.SensorEventRepository._select(participant_id, origin, from, to, limit)];
            case 4:
                output = (_b.data = _d.sent(), _b);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                _d.label = 5;
            case 5: return [3 /*break*/, 7];
            case 6:
                e_3 = _d.sent();
                console.log(e_3.message);
                if (e_3.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_3.message.split(".")[0]) || 500).json({ error: e_3.message });
                return [3 /*break*/, 7];
            case 7: return [2 /*return*/];
        }
    });
}); });
exports.SensorEventService.get("/study/:study_id/sensor_event", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var study_id, origin, from, to, limit, output, _a, e_4;
    var _b;
    return __generator(this, function (_c) {
        switch (_c.label) {
            case 0:
                _c.trys.push([0, 3, , 4]);
                study_id = req.params.study_id;
                origin = req.query.origin;
                from = Number.parse(req.query.from);
                to = Number.parse(req.query.to);
                limit = Math.min(Math.max((_b = Number.parse(req.query.limit), (_b !== null && _b !== void 0 ? _b : 1000)), -1000), 1000) // clamped to [-1000, 1000]
                ;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], study_id)];
            case 1:
                study_id = _c.sent();
                _a = {};
                return [4 /*yield*/, SensorEventRepository_1.SensorEventRepository._select(study_id, origin, from, to, limit)];
            case 2:
                output = (_a.data = _c.sent(), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_4 = _c.sent();
                if (e_4.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_4.message.split(".")[0]) || 500).json({ error: e_4.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.SensorEventService.get("/researcher/:researcher_id/sensor_event", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var researcher_id, origin, from, to, limit, output, _a, e_5;
    var _b;
    return __generator(this, function (_c) {
        switch (_c.label) {
            case 0:
                _c.trys.push([0, 3, , 4]);
                researcher_id = req.params.researcher_id;
                origin = req.query.origin;
                from = Number.parse(req.query.from);
                to = Number.parse(req.query.to);
                limit = Math.min(Math.max((_b = Number.parse(req.query.limit), (_b !== null && _b !== void 0 ? _b : 1000)), -1000), 1000) // clamped to [-1000, 1000]
                ;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], researcher_id)];
            case 1:
                researcher_id = _c.sent();
                _a = {};
                return [4 /*yield*/, SensorEventRepository_1.SensorEventRepository._select(researcher_id, origin, from, to, limit)];
            case 2:
                output = (_a.data = _c.sent(), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_5 = _c.sent();
                if (e_5.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_5.message.split(".")[0]) || 500).json({ error: e_5.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiU2Vuc29yRXZlbnRTZXJ2aWNlLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vc3JjL3NlcnZpY2UvU2Vuc29yRXZlbnRTZXJ2aWNlLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7O0FBQUEsbUNBQW1EO0FBRW5ELDZFQUEyRTtBQUMzRSx1Q0FBb0U7QUFDcEUsNkZBQWdIO0FBQ2hILG9EQUE2QjtBQUVoQixRQUFBLGtCQUFrQixHQUFHLGdCQUFNLEVBQUUsQ0FBQTtBQUMxQywwQkFBa0IsQ0FBQyxJQUFJLENBQUMsMkNBQTJDLEVBQUUsVUFBTyxHQUFZLEVBQUUsR0FBYTs7Ozs7O2dCQUUvRixjQUFjLEdBQUcsR0FBRyxDQUFDLE1BQU0sQ0FBQyxjQUFjLENBQUE7Z0JBQ3hDLFlBQVksR0FBRyxHQUFHLENBQUMsSUFBSSxDQUFBO3FCQUMxQixDQUFBLE9BQU8sQ0FBQyxHQUFHLENBQUMsVUFBVSxLQUFHLE1BQU0sQ0FBQSxFQUEvQix3QkFBK0I7O2dCQUV4QixxQkFBTSw2Q0FBZSxDQUFDLE9BQU8sQ0FDakMsY0FBYyxFQUNkLEtBQUssQ0FBQyxPQUFPLENBQUMsWUFBWSxDQUFDLENBQUMsQ0FBQyxDQUFDLFlBQVksQ0FBQyxDQUFDLENBQUMsQ0FBQyxZQUFZLENBQUMsQ0FDNUQsRUFBQTs7Z0JBSkcsV0FBVyxJQUNmLE9BQUksR0FBRSxTQUdMO3VCQUNGO2dCQUNELEdBQUcsQ0FBQyxJQUFJLENBQUMsV0FBVyxDQUFDLENBQUE7O29CQUVOLHFCQUFNLGtCQUFPLENBQUMsR0FBRyxDQUFDLEdBQUcsQ0FBQyxlQUFlLENBQUMsRUFBRSxDQUFDLE1BQU0sRUFBRSxTQUFTLEVBQUUsUUFBUSxDQUFDLEVBQUUsY0FBYyxDQUFDLEVBQUE7O2dCQUF2RyxjQUFjLEdBQUcsU0FBc0YsQ0FBQTs7Z0JBRS9GLHFCQUFNLDZDQUFxQixDQUFDLE9BQU8sQ0FDdkMsY0FBYyxFQUNk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