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
var ActivitySpecRepository_1 = require("../repository/ActivitySpecRepository");
var Security_1 = require("./Security");
var ActivitySpecRepository_2 = require("../repository/pouchRepository/ActivitySpecRepository");
var jsonata_1 = __importDefault(require("jsonata"));
exports.ActivitySpecService = express_1.Router();
exports.ActivitySpecService.post("/activity_spec", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var activity_spec, _, output, _a, e_1;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                activity_spec = req.body;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), [])];
            case 1:
                _ = _b.sent();
                _a = {};
                return [4 /*yield*/, ActivitySpecRepository_1.ActivitySpecRepository._insert(activity_spec)];
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
exports.ActivitySpecService.put("/activity_spec/:activity_spec_name", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var activity_spec_name, activity_spec, _, output, _a, e_2;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                activity_spec_name = req.params.activity_spec_name;
                activity_spec = req.body;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), [])];
            case 1:
                _ = _b.sent();
                _a = {};
                return [4 /*yield*/, ActivitySpecRepository_1.ActivitySpecRepository._update(activity_spec_name, activity_spec)];
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
exports.ActivitySpecService.delete("/activity_spec/:activity_spec_name", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var activity_spec_name, _, output, _a, e_3;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                activity_spec_name = req.params.activity_spec_name;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), [])];
            case 1:
                _ = _b.sent();
                _a = {};
                return [4 /*yield*/, ActivitySpecRepository_1.ActivitySpecRepository._delete(activity_spec_name)];
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
exports.ActivitySpecService.get("/activity_spec/:activity_spec_name", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var activity_spec_name, outputLocal, _a, _, output, _b, e_4;
    return __generator(this, function (_c) {
        switch (_c.label) {
            case 0:
                _c.trys.push([0, 6, , 7]);
                activity_spec_name = req.params.activity_spec_name;
                if (!(process.env.LOCAL_DATA === "true")) return [3 /*break*/, 2];
                _a = {};
                return [4 /*yield*/, ActivitySpecRepository_2.ActivitySpecRepository._select(activity_spec_name)];
            case 1:
                outputLocal = (_a.data = _c.sent(), _a);
                outputLocal = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(outputLocal) : outputLocal;
                res.json(outputLocal);
                return [3 /*break*/, 5];
            case 2: return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"])];
            case 3:
                _ = _c.sent();
                _b = {};
                return [4 /*yield*/, ActivitySpecRepository_1.ActivitySpecRepository._select(activity_spec_name)];
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
exports.ActivitySpecService.get("/activity_spec", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var outputLocal, _a, _, output, _b, e_5;
    return __generator(this, function (_c) {
        switch (_c.label) {
            case 0:
                _c.trys.push([0, 6, , 7]);
                if (!(process.env.LOCAL_DATA === "true")) return [3 /*break*/, 2];
                _a = {};
                return [4 /*yield*/, ActivitySpecRepository_2.ActivitySpecRepository._select()];
            case 1:
                outputLocal = (_a.data = _c.sent(), _a);
                outputLocal = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(outputLocal) : outputLocal;
                res.json(outputLocal);
                return [3 /*break*/, 5];
            case 2: return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"])];
            case 3:
                _ = _c.sent();
                _b = {};
                return [4 /*yield*/, ActivitySpecRepository_1.ActivitySpecRepository._select()];
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiQWN0aXZpdHlTcGVjU2VydmljZS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uL3NyYy9zZXJ2aWNlL0FjdGl2aXR5U3BlY1NlcnZpY2UudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7QUFBQSxtQ0FBbUQ7QUFFbkQsK0VBQTZFO0FBQzdFLHVDQUFvRTtBQUNwRSwrRkFBc0g7QUFDdEgsb0RBQTZCO0FBRWhCLFFBQUEsbUJBQW1CLEdBQUcsZ0JBQU0sRUFBRSxDQUFBO0FBQzNDLDJCQUFtQixDQUFDLElBQUksQ0FBQyxnQkFBZ0IsRUFBRSxVQUFPLEdBQVksRUFBRSxHQUFhOzs7Ozs7Z0JBRW5FLGFBQWEsR0FBRyxHQUFHLENBQUMsSUFBSSxDQUFBO2dCQUNwQixxQkFBTSxrQkFBTyxDQUFDLEdBQUcsQ0FBQyxHQUFHLENBQUMsZUFBZSxDQUFDLEVBQUUsRUFBRSxDQUFDLEVBQUE7O2dCQUEvQyxDQUFDLEdBQUcsU0FBMkM7O2dCQUM5QixxQkFBTSwrQ0FBc0IsQ0FBQyxPQUFPLENBQUMsYUFBYSxDQUFDLEVBQUE7O2dCQUFwRSxNQUFNLElBQUssT0FBSSxHQUFFLFNBQW1ELEtBQUU7Z0JBQzVFLEdBQUcsQ0FBQyxJQUFJLENBQUMsTUFBTSxDQUFDLENBQUE7Ozs7Z0JBRWhCLElBQUksR0FBQyxDQUFDLE9BQU8sS0FBSyx5QkFBeUI7b0JBQUUsR0FBRyxDQUFDLEdBQUcsQ0FBQyxrQkFBa0IsRUFBRSx3Q0FBb0MsQ0FBQyxDQUFBO2dCQUM5RyxHQUFHLENBQUMsTUFBTSxDQUFDLFFBQVEsQ0FBQyxHQUFDLENBQUMsT0FBTyxDQUFDLEtBQUssQ0FBQyxHQUFHLENBQUMsQ0FBQyxDQUFDLENBQUMsQ0FBQyxJQUFJLEdBQUcsQ0FBQyxDQUFDLElBQUksQ0FBQyxFQUFFLEtBQUssRUFBRSxHQUFDLENBQUMsT0FBTyxFQUFFLENBQUMsQ0FBQTs7Ozs7S0FFbEYsQ0FBQyxD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