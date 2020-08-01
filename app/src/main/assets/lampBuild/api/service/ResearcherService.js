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
var ResearcherRepository_1 = require("../repository/ResearcherRepository");
var Security_1 = require("./Security");
var jsonata_1 = __importDefault(require("jsonata"));
exports.ResearcherService = express_1.Router();
exports.ResearcherService.post("/researcher", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var researcher, _, output, _a, e_1;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                researcher = req.body;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), [])];
            case 1:
                _ = _b.sent();
                _a = {};
                return [4 /*yield*/, ResearcherRepository_1.ResearcherRepository._insert(researcher)];
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
exports.ResearcherService.put("/researcher/:researcher_id", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var researcher_id, researcher, output, _a, e_2;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                researcher_id = req.params.researcher_id;
                researcher = req.body;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], researcher_id)];
            case 1:
                researcher_id = _b.sent();
                _a = {};
                return [4 /*yield*/, ResearcherRepository_1.ResearcherRepository._update(researcher_id, researcher)];
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
exports.ResearcherService.delete("/researcher/:researcher_id", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var researcher_id, output, _a, e_3;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                researcher_id = req.params.researcher_id;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], researcher_id)];
            case 1:
                researcher_id = _b.sent();
                _a = {};
                return [4 /*yield*/, ResearcherRepository_1.ResearcherRepository._delete(researcher_id)];
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
exports.ResearcherService.get("/researcher/:researcher_id", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var researcher_id, output, _a, e_4;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                researcher_id = req.params.researcher_id;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], researcher_id)];
            case 1:
                researcher_id = _b.sent();
                _a = {};
                return [4 /*yield*/, ResearcherRepository_1.ResearcherRepository._select(researcher_id)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_4 = _b.sent();
                if (e_4.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_4.message.split(".")[0]) || 500).json({ error: e_4.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.ResearcherService.get("/researcher", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var _, output, _a, e_5;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), [])];
            case 1:
                _ = _b.sent();
                _a = {};
                return [4 /*yield*/, ResearcherRepository_1.ResearcherRepository._select()];
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiUmVzZWFyY2hlclNlcnZpY2UuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi9zcmMvc2VydmljZS9SZXNlYXJjaGVyU2VydmljZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUFBLG1DQUFtRDtBQUVuRCwyRUFBeUU7QUFDekUsdUNBQW9FO0FBQ3BFLG9EQUE2QjtBQUVoQixRQUFBLGlCQUFpQixHQUFHLGdCQUFNLEVBQUUsQ0FBQTtBQUN6Qyx5QkFBaUIsQ0FBQyxJQUFJLENBQUMsYUFBYSxFQUFFLFVBQU8sR0FBWSxFQUFFLEdBQWE7Ozs7OztnQkFFOUQsVUFBVSxHQUFHLEdBQUcsQ0FBQyxJQUFJLENBQUE7Z0JBQ2pCLHFCQUFNLGtCQUFPLENBQUMsR0FBRyxDQUFDLEdBQUcsQ0FBQyxlQUFlLENBQUMsRUFBRSxFQUFFLENBQUMsRUFBQTs7Z0JBQS9DLENBQUMsR0FBRyxTQUEyQzs7Z0JBQzlCLHFCQUFNLDJDQUFvQixDQUFDLE9BQU8sQ0FBQyxVQUFVLENBQUMsRUFBQTs7Z0JBQS9ELE1BQU0sSUFBSyxPQUFJLEdBQUUsU0FBOEMsS0FBRTtnQkFDdkUsR0FBRyxDQUFDLElBQUksQ0FBQyxNQUFNLENBQUMsQ0FBQTs7OztnQkFFaEIsSUFBSSxHQUFDLENBQUMsT0FBTyxLQUFLLHlCQUF5QjtvQkFBRSxHQUFHLENBQUMsR0FBRyxDQUFDLGtCQUFrQixFQUFFLHdDQUFvQyxDQUFDLENBQUE7Z0JBQzlHLEdBQUcsQ0FBQyxNQUFNLENBQUMsUUFBUSxDQUFDLEdBQUMsQ0FBQyxPQUFPLENBQUMsS0FBSyxDQUFDLEdBQUcsQ0FBQyxDQUFDLENBQUMsQ0FBQyxDQUFDLElBQUksR0FBRyxDQUFDLENBQUMsSUFBSSxDQUFDLEVBQUUsS0FBSyxFQUFFLEdBQUMsQ0FBQyxPQUFPLEVBQUUsQ0FBQyxDQUFBOzs7OztLQUVsRixDQUFDLENBQUE7QUFDRix5QkFBaUIsQ0FB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