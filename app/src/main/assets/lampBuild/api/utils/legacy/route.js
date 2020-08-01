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
var __read = (this && this.__read) || function (o, n) {
    var m = typeof Symbol === "function" && o[Symbol.iterator];
    if (!m) return o;
    var i = m.call(o), r, ar = [], e;
    try {
        while ((n === void 0 || n-- > 0) && !(r = i.next()).done) ar.push(r.value);
    }
    catch (error) { e = { error: error }; }
    finally {
        try {
            if (r && !r.done && (m = i["return"])) m.call(i);
        }
        finally { if (e) throw e.error; }
    }
    return ar;
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
/* eslint-disable @typescript-eslint/no-non-null-assertion */
var app_1 = require("../../app");
var migrate_1 = require("../../repository/migrate");
var mssql_1 = __importDefault(require("mssql"));
var express_1 = require("express");
var uuid_1 = require("uuid");
var nanoid_1 = require("nanoid");
var uuid = nanoid_1.customAlphabet("1234567890abcdefghjkmnpqrstvwxyz", 20); // crockford-32
exports.LegacyAPI = express_1.Router();
// Authenticate against legacy Bearer session tokens.
var _authorize = function (req, res, next) { return __awaiter(void 0, void 0, void 0, function () {
    var token, result;
    var _a, _b;
    return __generator(this, function (_c) {
        switch (_c.label) {
            case 0:
                token = (_a = req.headers["authorization"], (_a !== null && _a !== void 0 ? _a : "")).split(" ");
                if (!req.headers.hasOwnProperty("authorization") || token.length != 2 || token[0] != "Bearer") {
                    res.status(404).json({
                        ErrorCode: 2037,
                        ErrorMessage: "Your session has expired.",
                    });
                }
                return [4 /*yield*/, app_1.SQL.request().query("\n        SELECT \n            UserID, StudyId, Email, FirstName, LastName \n        FROM Users \n        WHERE SessionToken = '" + token[1] + "'\n    ;")];
            case 1:
                result = _c.sent();
                if (result.recordset.length == 0) {
                    res.status(404).json({
                        ErrorCode: 2037,
                        ErrorMessage: "Your session has expired.",
                    });
                }
                else {
                    ;
                    req.AuthUser = result.recordset[0];
                    req.AuthUser.StudyId = (_b = app_1.Decrypt(req.AuthUser.StudyId)) === null || _b === void 0 ? void 0 : _b.replace(/^G/, "");
                    next();
                }
                return [2 /*return*/];
        }
    });
}); };
// To convert legacy SQL IDs -> legacy API IDs -> new IDs
function _lookup_migrator_id(legacyID) {
    return __awaiter(this, void 0, void 0, function () {
        var _lookup_table, match;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, migrate_1._migrator_lookup_table()];
                case 1:
                    _lookup_table = _a.sent();
                    match = _lookup_table[legacyID];
                    if (match === undefined) {
                        match = uuid(); // 20-char id for non-Participant objects
                        _lookup_table[legacyID] = match;
                        console.log("inserting migrator link: " + legacyID + " => " + match);
                        app_1.Database.use("root").insert({ _id: "_local/" + legacyID, value: match });
                    }
                    return [2 /*return*/, match];
            }
        });
    });
}
// Route: /LogData
exports.LegacyAPI.post("/LogData", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        return [2 /*return*/, res.status(200).json({
                ErrorCode: 0,
                ErrorMessage: "Disabled",
            })];
    });
}); });
// Route: /SignIn // USES SQL
exports.LegacyAPI.post("/SignIn", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var requestData, Username, EncryptEmail, Email, Password, resultQuery, resultLength, userObj, UserId, AdminID, StudyId, Language, userSettingsData, CTestsFavouriteList, WelcomeText, InstructionVideoLink, SurveyFavouriteList, CognitionSettings, Data, SessionToken, DecryptedPswd, appendedSession, Type, updateUserSettings, userSettingsQuery, userSettingsLength, userSetting, defaultLanguage, AppColor, result, userSettingsQuery_1, userSettingsLength_1, userSetting, APPVersion, updateUserData, userDeviceQuery, updateUserDevice, insertUserDevice, loginEvent, out, FavouriteCtestQuery, FavouriteSurveyQuery, AdminSettingsQuery, CognitionSettingsQuery;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                requestData = req.body;
                Username = requestData.Username;
                EncryptEmail = app_1.Encrypt(requestData.Username);
                Email = requestData.Username;
                Password = requestData.Password;
                if (!Username) {
                    return [2 /*return*/, res.status(422).json({
                            ErrorCode: 2031,
                            ErrorMessage: "Specify Email Address.",
                        })];
                }
                if (!Password) {
                    return [2 /*return*/, res.status(422).json({
                            ErrorCode: 2031,
                            ErrorMessage: "Specify Password.",
                        })];
                }
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT UserID, AdminID, StudyId, Status, Password, IsDeleted, IsGuestUser FROM Users WHERE  Email = '" +
                        EncryptEmail +
                        "'")];
            case 1:
                resultQuery = _a.sent();
                resultLength = resultQuery.recordset.length;
                if (!(resultLength > 0)) return [3 /*break*/, 23];
                userObj = resultQuery.recordset[0];
                if (userObj.Status == null || userObj.Status == 0) {
                    return [2 /*return*/, res.status(200).json({
                            ErrorCode: 2044,
                            ErrorMessage: "This user has been deactivated. Please contact the administrator.",
                        })];
                }
                UserId = userObj.UserID;
                AdminID = userObj.AdminID;
                StudyId = app_1.Decrypt(userObj.StudyId);
                Language = requestData.Language;
                userSettingsData = {};
                CTestsFavouriteList = [];
                WelcomeText = "";
                InstructionVideoLink = "";
                SurveyFavouriteList = [];
                CognitionSettings = [];
                Data = {};
                SessionToken = "";
                DecryptedPswd = app_1.Decrypt(userObj.Password, "AES256");
                if (!(DecryptedPswd == Password)) return [3 /*break*/, 21];
                if (!(userObj.IsDeleted == 1)) return [3 /*break*/, 2];
                return [2 /*return*/, res.status(200).json({
                        ErrorCode: 2050,
                        ErrorMessage: "User account has been deleted.",
                    })];
            case 2:
                appendedSession = Username + ":" + Password;
                SessionToken = app_1.Encrypt(appendedSession);
                Type = userObj.IsGuestUser == 1 ? userObj.IsGuestUser : 0;
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("UPDATE UserSettings SET Language = '" + Language + "' WHERE UserID = " + UserId)];
            case 3:
                updateUserSettings = _a.sent();
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT UserSettingID, UserID, AppColor, SympSurvey_SlotID, SympSurvey_Time, SympSurvey_RepeatID, " +
                        " CognTest_SlotID, CognTest_Time, CognTest_RepeatID, [24By7ContactNo] ContactNo, PersonalHelpline, PrefferedSurveys, " +
                        " PrefferedCognitions, Protocol, Language, ProtocolDate " +
                        " FROM UserSettings WHERE UserID = " +
                        UserId)];
            case 4:
                userSettingsQuery = _a.sent();
                userSettingsLength = userSettingsQuery.recordset.length;
                if (!(userSettingsLength > 0)) return [3 /*break*/, 5];
                userSetting = userSettingsQuery.recordset[0];
                userSettingsData.UserSettingID = userSetting.UserSettingID;
                userSettingsData.UserID = userSetting.UserID;
                userSettingsData.AppColor = app_1.Decrypt(userSetting.AppColor);
                if (userSetting.SympSurvey_SlotID != null)
                    userSettingsData.SympSurveySlotID = userSetting.SympSurvey_SlotID;
                if (userSetting.SympSurvey_Time == null)
                    userSettingsData.SympSurveySlotTime = null;
                else
                    userSettingsData.SympSurveySlotTime = userSetting.SympSurvey_Time;
                if (userSetting.SympSurvey_RepeatID != null)
                    userSettingsData.SympSurveyRepeatID = userSetting.SympSurvey_RepeatID;
                if (userSetting.CognTest_SlotID != null)
                    userSettingsData.CognTestSlotID = userSetting.CognTest_SlotID;
                if (userSetting.CognTest_Time == null)
                    userSettingsData.CognTestSlotTime = null;
                else
                    userSettingsData.CognTestSlotTime = userSetting.CognTest_Time;
                if (userSetting.CognTest_RepeatID != null)
                    userSettingsData.CognTestRepeatID = userSetting.CognTest_RepeatID;
                userSettingsData.ContactNo =
                    userSetting.ContactNo === null || userSetting.ContactNo === "" ? "" : app_1.Decrypt(userSetting.ContactNo);
                userSettingsData.PersonalHelpline =
                    userSetting.PersonalHelpline === null || userSetting.PersonalHelpline === ""
                        ? ""
                        : app_1.Decrypt(userSetting.PersonalHelpline);
                userSettingsData.PrefferedSurveys =
                    userSetting.PrefferedSurveys === null || userSetting.PrefferedSurveys === ""
                        ? ""
                        : app_1.Decrypt(userSetting.PrefferedSurveys);
                userSettingsData.PrefferedCognitions =
                    userSetting.PrefferedCognitions === null || userSetting.PrefferedCognitions === ""
                        ? ""
                        : app_1.Decrypt(userSetting.PrefferedCognitions);
                userSettingsData.Protocol = userSetting.Protocol;
                userSettingsData.Language = userSetting.Language;
                userSettingsData.ProtocolDate = userSetting.Protocolstring; // Date
                return [3 /*break*/, 8];
            case 5:
                defaultLanguage = Language != "" ? Language : "en";
                AppColor = app_1.Encrypt("#359FFE");
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("INSERT into UserSettings (UserID, AppColor, SympSurvey_SlotID, SympSurvey_RepeatID, CognTest_SlotID, CognTest_RepeatID, [24By7ContactNo], PersonalHelpline, PrefferedSurveys, PrefferedCognitions, Language) VALUES (" +
                        UserId +
                        ", '" +
                        AppColor +
                        "', 1, 1, 1, 1, '', '', '', '', '" +
                        defaultLanguage +
                        "' ) ")];
            case 6:
                result = _a.sent();
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT UserSettingID, UserID, AppColor, SympSurvey_SlotID, SympSurvey_Time, SympSurvey_RepeatID, " +
                        " CognTest_SlotID, CognTest_Time, CognTest_RepeatID, [24By7ContactNo] ContactNo, PersonalHelpline, PrefferedSurveys, " +
                        " PrefferedCognitions, Protocol, Language, ProtocolDate " +
                        " FROM UserSettings WHERE UserID = " +
                        UserId)];
            case 7:
                userSettingsQuery_1 = _a.sent();
                userSettingsLength_1 = userSettingsQuery_1.recordset.length;
                if (userSettingsLength_1 > 0) {
                    userSetting = userSettingsQuery_1.recordset[0];
                    userSettingsData.UserSettingID = userSetting.UserSettingID;
                    userSettingsData.UserID = userSetting.UserID;
                    userSettingsData.AppColor = app_1.Decrypt(userSetting.AppColor);
                    if (userSetting.SympSurvey_SlotID != null)
                        userSettingsData.SympSurveySlotID = userSetting.SympSurvey_SlotID;
                    if (userSetting.SympSurvey_Time == null)
                        userSettingsData.SympSurveySlotTime = null;
                    else
                        userSettingsData.SympSurveySlotTime = userSetting.SympSurvey_Time.Value;
                    if (userSetting.SympSurvey_RepeatID != null)
                        userSettingsData.SympSurveyRepeatID = userSetting.SympSurvey_RepeatID;
                    if (userSetting.CognTest_SlotID != null)
                        userSettingsData.CognTestSlotID = userSetting.CognTest_SlotID;
                    if (userSetting.CognTest_Time == null)
                        userSettingsData.CognTestSlotTime = null;
                    else
                        userSettingsData.CognTestSlotTime = userSetting.CognTest_Time.Value;
                    if (userSetting.CognTest_RepeatID != null)
                        userSettingsData.CognTestRepeatID = userSetting.CognTest_RepeatID;
                    userSettingsData.ContactNo =
                        userSetting.ContactNo === null || userSetting.ContactNo === "" ? "" : app_1.Decrypt(userSetting.ContactNo);
                    userSettingsData.PersonalHelpline =
                        userSetting.PersonalHelpline === null || userSetting.PersonalHelpline === ""
                            ? ""
                            : app_1.Decrypt(userSetting.PersonalHelpline);
                    userSettingsData.PrefferedSurveys =
                        userSetting.PrefferedSurveys === null || userSetting.PrefferedSurveys === ""
                            ? ""
                            : app_1.Decrypt(userSetting.PrefferedSurveys);
                    userSettingsData.PrefferedCognitions =
                        userSetting.PrefferedCognitions === null || userSetting.PrefferedCognitions === ""
                            ? ""
                            : app_1.Decrypt(userSetting.PrefferedCognitions);
                    userSettingsData.Protocol = userSetting.Protocol;
                    userSettingsData.Language = userSetting.Language;
                    userSettingsData.ProtocolDate = userSetting.Protocolstring; // Date
                }
                _a.label = 8;
            case 8:
                Data = userSettingsData;
                APPVersion = app_1.Encrypt(requestData.APPVersion);
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("UPDATE Users SET SessionToken = '" +
                        SessionToken +
                        "' ,  APPVersion = '" +
                        APPVersion +
                        "' , EditedOn = GETUTCDATE() WHERE UserID = " +
                        UserId)];
            case 9:
                updateUserData = _a.sent();
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT UserDeviceID FROM UserDevices WHERE UserID = " + UserId + " ORDER By LastLoginOn DESC")];
            case 10:
                userDeviceQuery = _a.sent();
                if (!(userDeviceQuery.recordset.length > 0)) return [3 /*break*/, 12];
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("UPDATE UserDevices SET DeviceType = '" +
                        requestData.DeviceType +
                        "' , DeviceID = '" +
                        app_1.Encrypt(requestData.DeviceID) +
                        "', DeviceToken = '" +
                        app_1.Encrypt(requestData.DeviceToken) +
                        "' , LastLoginOn = GETUTCDATE() , OSVersion = '" +
                        requestData.OSVersion +
                        "' , DeviceModel = '" +
                        requestData.DeviceModel +
                        "' WHERE UserDeviceID = " +
                        userDeviceQuery.recordset[0].UserDeviceID)];
            case 11:
                updateUserDevice = _a.sent();
                return [3 /*break*/, 14];
            case 12: return [4 /*yield*/, app_1.SQL
                    .request()
                    .query("INSERT into UserDevices (UserID, DeviceType, DeviceID, DeviceToken, LastLoginOn, OSVersion, DeviceModel) VALUES (" +
                    UserId +
                    ", '" +
                    requestData.DeviceType +
                    "', '" +
                    app_1.Encrypt(requestData.DeviceID) +
                    "', '" +
                    app_1.Encrypt(requestData.DeviceToken) +
                    "', GETUTCDATE() , " +
                    requestData.OSVersion +
                    ", '" +
                    requestData.DeviceModel +
                    "' ) ")];
            case 13:
                insertUserDevice = _a.sent();
                _a.label = 14;
            case 14:
                loginEvent = {
                    "#parent": StudyId,
                    timestamp: new Date().getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: requestData.DeviceType == 1 ? "iOS" : requestData.DeviceType == 2 ? "Android" : "Unknown",
                        event_type: "login",
                        device_id: requestData.DeviceID,
                        device_token: requestData.DeviceToken,
                        os_version: requestData.OSVersion,
                        app_version: requestData.APPVersion,
                        device_model: requestData.DeviceModel,
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [loginEvent] })];
            case 15:
                out = _a.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT UserID, CTestID, FavType FROM UserFavouriteCTests WHERE UserID = " + UserId)];
            case 16:
                FavouriteCtestQuery = _a.sent();
                CTestsFavouriteList = FavouriteCtestQuery.recordset.length > 0 ? FavouriteCtestQuery.recordset : [];
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT UserID, SurveyID, FavType FROM UserFavouriteSurveys WHERE UserID = " + UserId)];
            case 17:
                FavouriteSurveyQuery = _a.sent();
                SurveyFavouriteList = FavouriteSurveyQuery.recordset.length > 0 ? FavouriteSurveyQuery.recordset : [];
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT WelcomeMessage, InstructionVideoLink FROM Admin_Settings WHERE AdminID = " + AdminID)];
            case 18:
                AdminSettingsQuery = _a.sent();
                WelcomeText = AdminSettingsQuery.recordset.length > 0 ? AdminSettingsQuery.recordset[0].WelcomeMessage : "";
                InstructionVideoLink =
                    AdminSettingsQuery.recordset.length > 0 ? AdminSettingsQuery.recordset[0].InstructionVideoLink : "";
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT a_ct.AdminCTestSettingID, a_ct.Notification, a_ct.AdminID, a_ct.CTestID, c_t.CTestName, NULL AS Status, NULL AS IconBlob, NULL AS Version, NULL AS MaxVersion FROM Admin_CTestSettings as a_ct JOIN CTest as c_t ON a_ct.CTestID = c_t.CTestID WHERE a_ct.AdminID = " +
                        AdminID +
                        " AND a_ct.Notification = 1  AND c_t.IsDeleted = 0")];
            case 19:
                CognitionSettingsQuery = _a.sent();
                CognitionSettings = CognitionSettingsQuery.recordset.length > 0 ? CognitionSettingsQuery.recordset : [];
                return [2 /*return*/, res.status(200).json({
                        UserId: UserId,
                        StudyId: StudyId,
                        Email: Email,
                        Type: Type,
                        SessionToken: SessionToken,
                        Data: Data,
                        ActivityPoints: {
                            SurveyPoint: 0,
                            _3DFigurePoint: 0,
                            CatAndDogPoint: 0,
                            CatAndDogNewPoint: 0,
                            DigitSpanForwardPoint: 0,
                            DigitSpanBackwardPoint: 0,
                            NBackPoint: 0,
                            Serial7Point: 0,
                            SimpleMemoryPoint: 0,
                            SpatialForwardPoint: 0,
                            SpatialBackwardPoint: 0,
                            TrailsBPoint: 0,
                            VisualAssociationPoint: 0,
                            TemporalOrderPoint: 0,
                            NBackNewPoint: 0,
                            TrailsBNewPoint: 0,
                            TrailsBDotTouchPoint: 0,
                            JewelsTrailsAPoint: 0,
                            JewelsTrailsBPoint: 0,
                        },
                        JewelsPoints: {
                            JewelsTrailsATotalBonus: 0,
                            JewelsTrailsBTotalBonus: 0,
                            JewelsTrailsATotalJewels: 0,
                            JewelsTrailsBTotalJewels: 0,
                        },
                        WelcomeText: WelcomeText,
                        InstructionVideoLink: InstructionVideoLink,
                        CognitionSettings: CognitionSettings,
                        CTestsFavouriteList: CTestsFavouriteList,
                        SurveyFavouriteList: SurveyFavouriteList,
                        ErrorCode: 0,
                        ErrorMessage: "The user has logged in successfully.",
                    })];
            case 20: return [3 /*break*/, 22];
            case 21: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 2034,
                    ErrorMessage: "Login failed. Please check the specified credentials.",
                })];
            case 22: return [3 /*break*/, 24];
            case 23: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 2034,
                    ErrorMessage: "Login failed. Please check the specified credentials.",
                })];
            case 24: return [2 /*return*/];
        }
    });
}); });
// Route: /ForgotPassword
exports.LegacyAPI.post("/ForgotPassword", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        return [2 /*return*/, res.status(400).json({
                ErrorCode: 400,
                ErrorMessage: "Disabled",
            })];
    });
}); });
// Route: /UserSignUp
exports.LegacyAPI.post("/UserSignUp", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        return [2 /*return*/, res.status(400).json({
                ErrorCode: 400,
                ErrorMessage: "Disabled",
            })];
    });
}); });
// Route: /GuestUserSignUp
exports.LegacyAPI.post("/GuestUserSignUp", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        return [2 /*return*/, res.status(400).json({
                ErrorCode: 400,
                ErrorMessage: "Disabled",
            })];
    });
}); });
// Route: /DeleteUser
exports.LegacyAPI.post("/DeleteUser", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        return [2 /*return*/, res.status(400).json({
                ErrorCode: 400,
                ErrorMessage: "Disabled",
            })];
    });
}); });
// Route: /UpdateUserProfile
exports.LegacyAPI.post("/UpdateUserProfile", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        return [2 /*return*/, res.status(400).json({
                ErrorCode: 2400,
                ErrorMessage: "Disabled",
            })];
    });
}); });
// Route: /GetUserProfile
exports.LegacyAPI.post("/GetUserProfile", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        return [2 /*return*/, res.status(200).json({
                Data: {
                    UserId: req.AuthUser.UserID,
                    FirstName: " ",
                    LastName: " ",
                    StudyId: req.AuthUser.StudyId,
                },
                ErrorCode: 0,
                ErrorMessage: "Disabled",
            })];
    });
}); });
// Route: /GetUserReport
exports.LegacyAPI.post("/GetUserReport", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        return [2 /*return*/, res.status(200).json({
                JewelsTrialsAList: [],
                JewelsTrialsBList: [],
                ErrorCode: 0,
                ErrorMessage: "Disabled",
            })];
    });
}); });
// Route: /GetProtocolDate
exports.LegacyAPI.post("/GetProtocolDate", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        return [2 /*return*/, res.status(200).json({
                ProtocolDate: new Date(0).toString(),
                ErrorCode: 0,
                ErrorMessage: "Disabled",
            })];
    });
}); });
// Route: /GetGameScoresforGraph
exports.LegacyAPI.post("/GetGameScoresforGraph", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        return [2 /*return*/, res.status(200).json({
                GameScoreList: [],
                ErrorCode: 0,
                ErrorMessage: "Disabled",
            })];
    });
}); });
// Route: /GetGameHighAndLowScoreforGraph
exports.LegacyAPI.post("/GetGameHighAndLowScoreforGraph", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        return [2 /*return*/, res.status(200).json({
                HighScore: "0",
                LowScore: "0",
                DayTotalScore: ["0"],
                ErrorCode: 0,
                ErrorMessage: "Disabled",
            })];
    });
}); });
// Route: /GetAllGameTotalSpinWheelScore
exports.LegacyAPI.post("/GetAllGameTotalSpinWheelScore", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        return [2 /*return*/, res.status(200).json({
                TotalScore: "0",
                CollectedStars: "0",
                DayStreak: 0,
                StrakSpin: 0,
                GameDate: new Date(0).toISOString().replace(/T/, " ").replace(/\..+/, ""),
                ErrorCode: 0,
                ErrorMessage: "Disabled",
            })];
    });
}); });
// Route: /GetUserCompletedSurvey
exports.LegacyAPI.post("/GetUserCompletedSurvey", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        return [2 /*return*/, res.status(200).json({
                CompletedSurveyList: [],
                ErrorCode: 0,
                ErrorMessage: "Disabled",
            })];
    });
}); });
// Route: /GetSurveyQueAndAns
exports.LegacyAPI.post("/GetSurveyQueAndAns", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        return [2 /*return*/, res.status(200).json({
                SurveyQueAndAnsList: [],
                ErrorCode: 0,
                ErrorMessage: "Disabled",
            })];
    });
}); });
// Route: /SaveUserSetting // USES SQL
exports.LegacyAPI.post("/SaveUserSetting", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var requestData, UserID, UserSettingID, resultQuery, resultCount, updateResult, result;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                requestData = req.body;
                UserID = requestData.UserID;
                if (!UserID || !Number.isInteger(Number(UserID)) || UserID == 0) {
                    return [2 /*return*/, res.status(422).json({
                            ErrorCode: 2031,
                            ErrorMessage: "Specify valid User Id.",
                        })];
                }
                UserSettingID = requestData.UserSettingID;
                if (!(UserSettingID > 0)) return [3 /*break*/, 5];
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT COUNT(UserID) as count FROM UserSettings WHERE UserSettingID = " + UserSettingID)];
            case 1:
                resultQuery = _a.sent();
                resultCount = resultQuery.recordset[0].count;
                if (!(resultCount > 0)) return [3 /*break*/, 3];
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("UPDATE UserSettings SET AppColor = '" +
                        app_1.Encrypt(requestData.AppColor) +
                        "', SympSurvey_SlotID = " +
                        requestData.SympSurveySlotID +
                        ", SympSurvey_Time = '" +
                        requestData.SympSurveySlotTime +
                        "' , SympSurvey_RepeatID = " +
                        requestData.SympSurveyRepeatID +
                        ", CognTest_SlotID = " +
                        requestData.CognTestSlotID +
                        ", CognTest_Time = '" +
                        requestData.CognTestSlotTime +
                        "', CognTest_RepeatID = " +
                        requestData.CognTestRepeatID +
                        ", [24By7ContactNo] = '" +
                        app_1.Encrypt(requestData.ContactNo) +
                        "', PersonalHelpline = '" +
                        app_1.Encrypt(requestData.PersonalHelpline) +
                        "', PrefferedSurveys = '" +
                        app_1.Encrypt(requestData.PrefferedSurveys) +
                        "', PrefferedCognitions = '" +
                        app_1.Encrypt(requestData.PrefferedCognitions) +
                        "', Protocol = '" +
                        requestData.Protocol +
                        "', Language = '" +
                        requestData.Language +
                        "' WHERE UserSettingID = " +
                        UserSettingID)];
            case 2:
                updateResult = _a.sent();
                if (updateResult.rowsAffected[0] > 0) {
                    return [2 /*return*/, res.status(200).json({
                            ErrorCode: 0,
                            ErrorMessage: "The user settings have been saved successfully.",
                        })];
                }
                else {
                    return [2 /*return*/, res.status(500).json({
                            ErrorCode: 2030,
                            ErrorMessage: "An error occured while updating data.",
                        })];
                }
                return [3 /*break*/, 4];
            case 3: return [2 /*return*/, res.status(422).json({
                    Data: {},
                    ErrorCode: 2031,
                    ErrorMessage: "Specify valid User Setting Id.",
                })];
            case 4: return [3 /*break*/, 7];
            case 5: return [4 /*yield*/, app_1.SQL
                    .request()
                    .query("INSERT into UserSettings (UserID, AppColor, SympSurvey_SlotID, SympSurvey_Time, SympSurvey_RepeatID, CognTest_SlotID, CognTest_Time, CognTest_RepeatID, [24By7ContactNo], PersonalHelpline, PrefferedSurveys, PrefferedCognitions, Protocol, Language) VALUES (" +
                    UserID +
                    ", '" +
                    app_1.Encrypt(requestData.AppColor) +
                    "', " +
                    requestData.SympSurveySlotID +
                    ", '" +
                    requestData.SympSurveySlotTime +
                    "', " +
                    requestData.SympSurveyRepeatID +
                    ", " +
                    requestData.CognTestSlotID +
                    ", '" +
                    requestData.CognTestSlotTime +
                    "', " +
                    requestData.CognTestRepeatID +
                    ", '" +
                    app_1.Encrypt(requestData.ContactNo) +
                    "', '" +
                    app_1.Encrypt(requestData.PersonalHelpline) +
                    "', '" +
                    app_1.Encrypt(requestData.PrefferedSurveys) +
                    "', '" +
                    app_1.Encrypt(requestData.PrefferedCognitions) +
                    "', '" +
                    requestData.Protocol +
                    "', '" +
                    requestData.Language +
                    "' ) ")];
            case 6:
                result = _a.sent();
                return [2 /*return*/, res.status(200).json({
                        ErrorCode: 0,
                        ErrorMessage: "The user settings have been saved successfully.",
                    })];
            case 7: return [2 /*return*/];
        }
    });
}); });
// Route: /GetUserSetting // USES SQL
exports.LegacyAPI.post("/GetUserSetting", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var requestData, UserID, result, objData, resultData, appColor, ContactNo, PersonalHelpline, PrefferedSurveys, PrefferedCognitions, objData1;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                requestData = req.body;
                UserID = requestData.UserID;
                if (!UserID || !Number.isInteger(Number(UserID)) || UserID == 0) {
                    return [2 /*return*/, res.status(422).json({
                            Data: {},
                            ErrorCode: 2031,
                            ErrorMessage: "Specify valid User Id.",
                        })];
                }
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query('SELECT UserSettingID, UserID, AppColor, SympSurvey_SlotID SympSurveySlotID, SympSurvey_Time SympSurveySlotTime, SympSurvey_RepeatID SympSurveyRepeatID, CognTest_SlotID CognTestSlotID, CognTest_Time CognTestSlotTime, CognTest_RepeatID CognTestRepeatID, "24By7ContactNo" ContactNo, PersonalHelpline, PrefferedSurveys, PrefferedCognitions, Protocol, ProtocolDate, Language FROM UserSettings WHERE UserID =' +
                        UserID)];
            case 1:
                result = _a.sent();
                objData = {};
                if (result.recordset.length >= 0 && result.recordset[0] != null) {
                    resultData = result.recordset[0];
                    appColor = resultData.AppColor;
                    ContactNo = resultData.ContactNo;
                    PersonalHelpline = resultData.PersonalHelpline;
                    PrefferedSurveys = resultData.PrefferedSurveys;
                    PrefferedCognitions = resultData.PrefferedCognitions;
                    objData1 = resultData;
                    delete objData1.AppColor;
                    delete objData1.ContactNo;
                    delete objData1.PersonalHelpline;
                    delete objData1.PrefferedSurveys;
                    delete objData1.PrefferedCognitions;
                    objData1.AppColor = app_1.Decrypt(appColor);
                    objData1.ContactNo = app_1.Decrypt(ContactNo);
                    objData1.PersonalHelpline = app_1.Decrypt(PersonalHelpline);
                    objData1.PrefferedSurveys = app_1.Decrypt(PrefferedSurveys);
                    objData1.PrefferedCognitions = app_1.Decrypt(PrefferedCognitions);
                    objData = objData1;
                }
                return [2 /*return*/, res.status(200).json({
                        Data: objData,
                        ErrorCode: 0,
                        ErrorMessage: "User Setting Details",
                    })];
        }
    });
}); });
// Route: /SaveUserCTestsFavourite // USES SQL
exports.LegacyAPI.post("/SaveUserCTestsFavourite", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var requestData, UserID, CTestID, FavType, Type, result, result;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                requestData = req.body;
                UserID = requestData.UserId;
                if (!UserID || !Number.isInteger(Number(UserID)) || UserID == 0) {
                    return [2 /*return*/, res.status(422).json({
                            ErrorCode: 2031,
                            ErrorMessage: "Specify valid User Id.",
                        })];
                }
                CTestID = requestData.CTestID;
                if (!Number.isInteger(CTestID)) {
                    return [2 /*return*/, res.status(422).json({
                            ErrorCode: 2031,
                            ErrorMessage: "Specify valid CTestID.",
                        })];
                }
                FavType = requestData.FavType;
                if (!Number.isInteger(FavType)) {
                    return [2 /*return*/, res.status(422).json({
                            ErrorCode: 2031,
                            ErrorMessage: "Specify valid FavType.",
                        })];
                }
                Type = requestData.Type;
                if (Type != 1 && Type != 2) {
                    return [2 /*return*/, res.status(422).json({
                            ErrorCode: 2031,
                            ErrorMessage: "Specify valid Type.",
                        })];
                }
                if (!(Type == 1)) return [3 /*break*/, 2];
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("INSERT into UserFavouriteSurveys (UserID, SurveyID, FavType) VALUES (" +
                        UserID +
                        "," +
                        requestData.CTestID +
                        "," +
                        requestData.FavType +
                        ") ")];
            case 1:
                result = _a.sent();
                return [3 /*break*/, 4];
            case 2: return [4 /*yield*/, app_1.SQL
                    .request()
                    .query("INSERT into UserFavouriteCTests (UserID, CTestID, FavType) VALUES (" +
                    UserID +
                    "," +
                    requestData.CTestID +
                    "," +
                    requestData.FavType +
                    ") ")];
            case 3:
                result = _a.sent();
                _a.label = 4;
            case 4: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "User CTests Favourite Saved.",
                })];
        }
    });
}); });
// Route: /GetTips // USES SQL
exports.LegacyAPI.post("/GetTips", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var requestData, UserID, resultData, result;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                requestData = req.body;
                UserID = requestData.UserID;
                if (!UserID || !Number.isInteger(Number(UserID)) || UserID == 0) {
                    return [2 /*return*/, res.status(422).json({
                            ErrorCode: 2031,
                            ErrorMessage: "Specify valid User Id.",
                        })];
                }
                resultData = "";
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT TipText FROM Tips WHERE IsDeleted = 0 AND AdminID IN (SELECT AdminID FROM Users WHERE UserID = " +
                        UserID +
                        ");")];
            case 1:
                result = _a.sent();
                if (result.recordset.length >= 0 && result.recordset[0] != null) {
                    resultData = result.recordset[0].TipText;
                }
                return [2 /*return*/, res.status(200).json({
                        TipText: resultData,
                        ErrorCode: 0,
                        ErrorMessage: "Listing the Tips List.",
                    })];
        }
    });
}); });
// Route: /GetBlogs // USES SQL
exports.LegacyAPI.post("/GetBlogs", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var requestData, UserID, objData, result, resultData, k;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                requestData = req.body;
                UserID = requestData.UserID;
                if (!UserID || !Number.isInteger(Number(UserID)) || UserID == 0) {
                    return [2 /*return*/, res.status(422).json({
                            ErrorCode: 2031,
                            ErrorMessage: "Specify valid User Id.",
                        })];
                }
                objData = [];
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT BlogTitle, BlogText, Content, ImageURL FROM Blogs WHERE IsDeleted = 0 AND AdminID IN (SELECT AdminID FROM Users WHERE UserID = " +
                        UserID +
                        ");")];
            case 1:
                result = _a.sent();
                if (result.recordset.length >= 0) {
                    resultData = result.recordset;
                    for (k = 0; k < result.recordset.length; k++) {
                        objData[k] = {
                            BlogTitle: resultData[k].BlogTitle,
                            Content: resultData[k].Content,
                            ImageURL: "https://s3.us-east-2.amazonaws.com/" + app_1.AWSBucketName + "/BlogImages/" + app_1.Decrypt(resultData[k].ImageURL),
                            BlogText: resultData[k].BlogText,
                        };
                    }
                }
                return [2 /*return*/, res.status(200).json({
                        BlogList: objData,
                        ErrorCode: 0,
                        ErrorMessage: "Listing the Blog details.",
                    })];
        }
    });
}); });
// Route: /GetTipsandBlogsUpdates // USES SQL
exports.LegacyAPI.post("/GetTipsandBlogUpdates", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var requestData, UserID, BlogsUpdate, TipsUpdate, resultQuery, AdminID, BlogsViewedOn, TipsViewedOn, blogQuery, i, tipsQuery, i;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                requestData = req.body;
                UserID = requestData.UserID;
                if (!UserID || !Number.isInteger(Number(UserID)) || UserID == 0) {
                    return [2 /*return*/, res.status(422).json({
                            ErrorCode: 2031,
                            ErrorMessage: "Specify valid User Id.",
                        })];
                }
                BlogsUpdate = false;
                TipsUpdate = false;
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT AdminID, BlogsViewedOn, TipsViewedOn FROM Users as u_s JOIN UserSettings as us_s " +
                        "ON u_s.UserID = us_s.UserID WHERE u_s.IsDeleted = 0 AND u_s.UserID = " +
                        UserID)];
            case 1:
                resultQuery = _a.sent();
                if (!(resultQuery.recordset.length > 0)) return [3 /*break*/, 4];
                AdminID = resultQuery.recordset[0].AdminID;
                BlogsViewedOn = resultQuery.recordset[0].BlogsViewedOn;
                TipsViewedOn = resultQuery.recordset[0].TipsViewedOn;
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT CreatedOn, EditedOn FROM Blogs WHERE IsDeleted = 0 AND AdminID = " + AdminID)];
            case 2:
                blogQuery = _a.sent();
                if (blogQuery.recordset.length > 0) {
                    for (i = 0; i < blogQuery.recordset.length; i++) {
                        if (blogQuery.recordset[i].CreatedOn !== null && blogQuery.recordset[i].EditedOn !== null) {
                            if (BlogsViewedOn < blogQuery.recordset[i].CreatedOn || BlogsViewedOn < blogQuery.recordset[i].EditedOn) {
                                BlogsUpdate = true;
                                break;
                            }
                        }
                    }
                }
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT CreatedOn, EditedOn FROM Tips WHERE IsDeleted = 0 AND AdminID = " + AdminID)];
            case 3:
                tipsQuery = _a.sent();
                if (tipsQuery.recordset.length > 0) {
                    for (i = 0; i < tipsQuery.recordset.length; i++) {
                        if (tipsQuery.recordset[i].CreatedOn !== null && tipsQuery.recordset[i].EditedOn !== null) {
                            if (TipsViewedOn < tipsQuery.recordset[i].CreatedOn || TipsViewedOn < tipsQuery.recordset[i].EditedOn) {
                                TipsUpdate = true;
                                break;
                            }
                        }
                    }
                }
                _a.label = 4;
            case 4: return [2 /*return*/, res.status(200).json({
                    BlogsUpdate: BlogsUpdate,
                    TipsUpdate: TipsUpdate,
                    ErrorCode: 0,
                    ErrorMessage: "Listing the Tips and Blog Updates Detail.",
                })];
        }
    });
}); });
// Route: /GetAppHelp // USES SQL
exports.LegacyAPI.post("/GetAppHelp", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var requestData, UserID, HelpText, Content, ImageURL, result;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                requestData = req.body;
                UserID = requestData.UserID;
                if (!UserID || !Number.isInteger(Number(UserID)) || UserID == 0) {
                    return [2 /*return*/, res.status(422).json({
                            ErrorCode: 2031,
                            ErrorMessage: "Specify valid User Id.",
                        })];
                }
                ImageURL = "";
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT HelpText, Content, ImageURL FROM AppHelp WHERE IsDeleted = 0 AND AdminID IN (SELECT AdminID FROM Users WHERE UserID = " +
                        UserID +
                        ");")];
            case 1:
                result = _a.sent();
                if (result.recordset.length >= 0 && result.recordset[0] != null) {
                    HelpText = result.recordset[0].HelpText;
                    Content = result.recordset[0].Content;
                    ImageURL = "https://s3.us-east-2.amazonaws.com/" + app_1.AWSBucketName + "/AppHelpImages/" + app_1.Decrypt(result.recordset[0].ImageURL);
                }
                return [2 /*return*/, res.status(200).json({
                        HelpText: HelpText,
                        Content: Content,
                        ImageURL: ImageURL,
                        ErrorCode: 0,
                        ErrorMessage: "Listing the App Help Details.",
                    })];
        }
    });
}); });
// Route: /GetSurveyAndGameSchedule // USES SQL
exports.LegacyAPI.post("/GetSurveyAndGameSchedule", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var requestData, UserID, ReminderClearInterval, JewelsTrailsASettings, JewelsTrailsBSettings, CognitionOffList, CognitionIconList, SurveyIconList, CognitionVersionList, ScheduleSurveyList, ScheduleGameList, BatchScheduleList, ContactNo, PersonalHelpline, LastUpdatedSurveyDate, LastUpdatedGameDate, LastUpdatedBatchDate, resultQuery, AdminID, UserSettingsQuery, AdminSettingsQuery, JewelsASettingQuery, JewelsBSettingQuery, CognitionOffListQuery, CognitionOffListArray, eachCognitionOffList, CognitionOffListArray_1, i, n, CognitionIconListQuery, CognitionIconListArray, eachCognitionIconList, CognitionIconListArray_1, i, n, SurveyIconListQuery, SurveyIconListArray, eachSurveyIconList, SurveyIconListArray_1, i, n, CognitionVersionListQuery, ScheduleSurveyListQuery, scheduleSurveyListOutput, p_ErrID, scheduleSurvArray, eachSchedule, i, n, eachslotTimeOptions, slotTimeArray, j, n_1, scheduleGameListQuery, scheduleGameListOutput, p_ErrID, scheduleGameArray, eachScheduleGame, i, n, eachslotTimeOptions, slotTimeArray, j, n_2, batchScheduleListQuery_1, batchScheduleListOutput, p_ErrID, batchScheduleArray, eachBatchSchedule_1, _loop_1, i, n;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                requestData = req.body;
                UserID = requestData.UserID;
                if (!UserID || !Number.isInteger(Number(UserID)) || UserID == 0) {
                    return [2 /*return*/, res.status(422).json({
                            ErrorCode: 2031,
                            ErrorMessage: "Specify valid User Id.",
                        })];
                }
                ReminderClearInterval = 0;
                JewelsTrailsASettings = {};
                JewelsTrailsBSettings = {};
                CognitionOffList = [];
                CognitionIconList = [];
                SurveyIconList = [];
                CognitionVersionList = [];
                ScheduleSurveyList = [];
                ScheduleGameList = [];
                BatchScheduleList = [];
                ContactNo = "";
                PersonalHelpline = "";
                LastUpdatedSurveyDate = "";
                LastUpdatedGameDate = "";
                LastUpdatedBatchDate = "";
                return [4 /*yield*/, app_1.SQL.request().query("SELECT AdminID FROM Users WHERE IsDeleted = 0 AND UserID = " + UserID)];
            case 1:
                resultQuery = _a.sent();
                if (!(resultQuery.recordset.length > 0)) return [3 /*break*/, 13];
                AdminID = resultQuery.recordset[0].AdminID;
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT [24By7ContactNo] ContactNo, PersonalHelpline FROM UserSettings WHERE UserID = " + UserID)];
            case 2:
                UserSettingsQuery = _a.sent();
                ContactNo =
                    UserSettingsQuery.recordset.length > 0
                        ? UserSettingsQuery.recordset[0].ContactNo === null || UserSettingsQuery.recordset[0].ContactNo === ""
                            ? ""
                            : app_1.Decrypt(UserSettingsQuery.recordset[0].ContactNo)
                        : "";
                PersonalHelpline =
                    UserSettingsQuery.recordset.length > 0
                        ? UserSettingsQuery.recordset[0].PersonalHelpline === null ||
                            UserSettingsQuery.recordset[0].PersonalHelpline === ""
                            ? ""
                            : app_1.Decrypt(UserSettingsQuery.recordset[0].PersonalHelpline)
                        : "";
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT ReminderClearInterval FROM Admin_Settings WHERE AdminID = " + AdminID)];
            case 3:
                AdminSettingsQuery = _a.sent();
                ReminderClearInterval =
                    AdminSettingsQuery.recordset.length > 0 ? AdminSettingsQuery.recordset[0].ReminderClearInterval : 0;
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT NoOfSeconds_Beg, NoOfSeconds_Int, NoOfSeconds_Adv, NoOfSeconds_Exp, NoOfDiamonds, NoOfShapes, NoOfBonusPoints, X_NoOfChangesInLevel, X_NoOfDiamonds, Y_NoOfChangesInLevel, Y_NoOfShapes FROM Admin_JewelsTrailsASettings WHERE AdminID = " +
                        AdminID)];
            case 4:
                JewelsASettingQuery = _a.sent();
                JewelsTrailsASettings = JewelsASettingQuery.recordset.length > 0 ? JewelsASettingQuery.recordset[0] : {};
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT NoOfSeconds_Beg, NoOfSeconds_Int, NoOfSeconds_Adv, NoOfSeconds_Exp, NoOfDiamonds, NoOfShapes, NoOfBonusPoints, X_NoOfChangesInLevel, X_NoOfDiamonds, Y_NoOfChangesInLevel, Y_NoOfShapes FROM Admin_JewelsTrailsBSettings WHERE AdminID = " +
                        AdminID)];
            case 5:
                JewelsBSettingQuery = _a.sent();
                JewelsTrailsBSettings = JewelsBSettingQuery.recordset.length > 0 ? JewelsBSettingQuery.recordset[0] : {};
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT a_ct.AdminCTestSettingID, a_ct.Notification, a_ct.IconBlob, a_ct.Version, a_ct.AdminID, a_ct.CTestID, c_t.MaxVersions, c_t.CTestName, a_ct.Status FROM Admin_CTestSettings as a_ct JOIN CTest as c_t ON a_ct.CTestID = c_t.CTestID WHERE a_ct.AdminID = " +
                        AdminID +
                        " AND a_ct.Status = 1  AND c_t.IsDeleted = 0")];
            case 6:
                CognitionOffListQuery = _a.sent();
                if (CognitionOffListQuery.recordset.length > 0) {
                    CognitionOffListArray = [];
                    eachCognitionOffList = void 0;
                    CognitionOffListArray_1 = void 0;
                    for (i = 0, n = CognitionOffListQuery.recordset.length; i < n; ++i) {
                        eachCognitionOffList = CognitionOffListQuery.recordset[i];
                        CognitionOffListArray_1 = {
                            AdminCTestSettingID: parseInt(eachCognitionOffList.AdminCTestSettingID),
                            AdminID: parseInt(eachCognitionOffList.AdminID),
                            CTestID: parseInt(eachCognitionOffList.CTestID),
                            CTestName: eachCognitionOffList.CTestName,
                            Status: eachCognitionOffList.Status,
                            Notification: eachCognitionOffList.Notification,
                            IconBlob: eachCognitionOffList.IconBlob != null
                                ? Buffer.from(eachCognitionOffList.IconBlob).toString("base64")
                                : null,
                            Version: eachCognitionOffList.Version,
                            MaxVersion: eachCognitionOffList.MaxVersions,
                        };
                        CognitionOffListArray[i] = CognitionOffListArray_1;
                    }
                    CognitionOffList = CognitionOffListArray;
                }
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT AdminCTestSettingID, AdminID, CTestID , IconBlob FROM Admin_CTestSettings WHERE AdminID = " + AdminID)];
            case 7:
                CognitionIconListQuery = _a.sent();
                if (CognitionIconListQuery.recordset.length > 0) {
                    CognitionIconListArray = [];
                    eachCognitionIconList = void 0;
                    CognitionIconListArray_1 = void 0;
                    for (i = 0, n = CognitionIconListQuery.recordset.length; i < n; ++i) {
                        eachCognitionIconList = CognitionIconListQuery.recordset[i];
                        CognitionIconListArray_1 = {
                            AdminCTestSettingID: parseInt(eachCognitionIconList.AdminCTestSettingID),
                            AdminID: parseInt(eachCognitionIconList.AdminID),
                            CTestID: parseInt(eachCognitionIconList.CTestID),
                            IconBlob: eachCognitionIconList.IconBlob != null
                                ? Buffer.from(eachCognitionIconList.IconBlob).toString("base64")
                                : null,
                            IconBlobString: eachCognitionIconList.IconBlob != null
                                ? Buffer.from(eachCognitionIconList.IconBlob).toString("base64")
                                : null,
                        };
                        CognitionIconListArray[i] = CognitionIconListArray_1;
                    }
                    CognitionIconList = CognitionIconListArray;
                }
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT  AdminID, SurveyID , IconBlob FROM Survey WHERE AdminID = " + AdminID)];
            case 8:
                SurveyIconListQuery = _a.sent();
                if (SurveyIconListQuery.recordset.length > 0) {
                    SurveyIconListArray = [];
                    eachSurveyIconList = void 0;
                    SurveyIconListArray_1 = void 0;
                    for (i = 0, n = SurveyIconListQuery.recordset.length; i < n; ++i) {
                        eachSurveyIconList = SurveyIconListQuery.recordset[i];
                        SurveyIconListArray_1 = {
                            SurveyID: parseInt(eachSurveyIconList.SurveyID),
                            AdminID: parseInt(eachSurveyIconList.AdminID),
                            IconBlob: eachSurveyIconList.IconBlob != null ? Buffer.from(eachSurveyIconList.IconBlob).toString("base64") : null,
                            IconBlobString: eachSurveyIconList.IconBlob != null ? Buffer.from(eachSurveyIconList.IconBlob).toString("base64") : null,
                        };
                        SurveyIconListArray[i] = SurveyIconListArray_1;
                    }
                    SurveyIconList = SurveyIconListArray;
                }
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT a_ct.CTestID, c_t.CTestName, a_ct.Version FROM Admin_CTestSettings as a_ct JOIN CTest as c_t ON a_ct.CTestID = c_t.CTestID WHERE a_ct.AdminID = " +
                        AdminID +
                        " AND a_ct.Version IS NOT NULL")];
            case 9:
                CognitionVersionListQuery = _a.sent();
                CognitionVersionList = CognitionVersionListQuery.recordset.length > 0 ? CognitionVersionListQuery.recordset[0] : [];
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .input("p_UserID", UserID)
                        .input("p_LastFetchedTS", requestData.LastUpdatedSurveyDate)
                        .output("p_ErrID", mssql_1.default.VarChar(10))
                        .output("p_LastUpdatedTS", mssql_1.default.DateTime)
                        .execute("GetAdminSurveyScheduleByUserID_sp")];
            case 10:
                ScheduleSurveyListQuery = _a.sent();
                scheduleSurveyListOutput = ScheduleSurveyListQuery.output;
                if (Object.keys(scheduleSurveyListOutput).length > 0) {
                    p_ErrID = scheduleSurveyListOutput.p_ErrID;
                    LastUpdatedSurveyDate =
                        scheduleSurveyListOutput.p_LastUpdatedTS != null
                            ? new Date(scheduleSurveyListOutput.p_LastUpdatedTS).toISOString().replace(/T/, " ").replace(/\..+/, "")
                            : null;
                    if (p_ErrID == 0) {
                        scheduleSurvArray = [];
                        if (ScheduleSurveyListQuery.recordsets[0].length > 0) {
                            eachSchedule = void 0;
                            for (i = 0, n = ScheduleSurveyListQuery.recordsets[0].length; i < n; ++i) {
                                eachSchedule = ScheduleSurveyListQuery.recordsets[0][i];
                                eachslotTimeOptions = void 0;
                                slotTimeArray = [];
                                for (j = 0, n_1 = ScheduleSurveyListQuery.recordsets[1].length; j < n_1; ++j) {
                                    eachslotTimeOptions = ScheduleSurveyListQuery.recordsets[1][j];
                                    if (eachSchedule.SurveyScheduleID == eachslotTimeOptions.ScheduleID) {
                                        slotTimeArray.push(eachslotTimeOptions.Time != null
                                            ? new Date(eachslotTimeOptions.Time).toISOString().replace(/T/, " ").replace(/\..+/, "")
                                            : null);
                                    }
                                }
                                scheduleSurvArray.push({
                                    SurveyScheduleID: parseInt(eachSchedule.SurveyScheduleID),
                                    SurveyName: eachSchedule.SurveyName,
                                    Time: eachSchedule.Time,
                                    SlotTime: eachSchedule.Time != null
                                        ? new Date(eachSchedule.Time).toISOString().replace(/T/, " ").replace(/\..+/, "")
                                        : null,
                                    SurveyId: parseInt(eachSchedule.SurveyId),
                                    ScheduleDate: eachSchedule.ScheduleDate,
                                    RepeatID: parseInt(eachSchedule.RepeatID),
                                    IsDeleted: eachSchedule.IsDeleted,
                                    SlotTimeOptions: slotTimeArray,
                                });
                                if (eachSchedule.RepeatID == 5 ||
                                    eachSchedule.RepeatID == 6 ||
                                    eachSchedule.RepeatID == 7 ||
                                    eachSchedule.RepeatID == 8 ||
                                    eachSchedule.RepeatID == 9 ||
                                    eachSchedule.RepeatID == 10 ||
                                    eachSchedule.RepeatID == 12) {
                                    scheduleSurvArray.ScheduleDate = eachSchedule.Time;
                                }
                            }
                            ScheduleSurveyList = scheduleSurvArray;
                        }
                    }
                    else {
                        return [2 /*return*/, res.status(500).json({
                                ErrorCode: 2030,
                                ErrorMessage: "An error occured on Store procedure `GetAdminSurveyScheduleByUserID_sp`.",
                            })];
                    }
                }
                else {
                    return [2 /*return*/, res.status(500).json({
                            ErrorCode: 2030,
                            ErrorMessage: "An error occured on Store procedure `GetAdminSurveyScheduleByUserID_sp`.",
                        })];
                }
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .input("p_UserID", UserID)
                        .input("p_LastFetchedTS", requestData.LastUpdatedGameDate)
                        .output("p_ErrID", mssql_1.default.VarChar(10))
                        .output("p_LastUpdatedTS", mssql_1.default.DateTime)
                        .execute("GetAdminCTestScheduleByUserID_sp")];
            case 11:
                scheduleGameListQuery = _a.sent();
                scheduleGameListOutput = scheduleGameListQuery.output;
                if (Object.keys(scheduleGameListOutput).length > 0) {
                    p_ErrID = scheduleGameListOutput.p_ErrID;
                    LastUpdatedGameDate =
                        scheduleGameListOutput.p_LastUpdatedTS != null
                            ? new Date(scheduleGameListOutput.p_LastUpdatedTS).toISOString().replace(/T/, " ").replace(/\..+/, "")
                            : null;
                    if (p_ErrID == 0) {
                        scheduleGameArray = [];
                        if (scheduleGameListQuery.recordsets[0].length > 0) {
                            eachScheduleGame = void 0;
                            for (i = 0, n = scheduleGameListQuery.recordsets[0].length; i < n; ++i) {
                                eachScheduleGame = scheduleGameListQuery.recordsets[0][i];
                                eachslotTimeOptions = void 0;
                                slotTimeArray = [];
                                for (j = 0, n_2 = scheduleGameListQuery.recordsets[1].length; j < n_2; ++j) {
                                    eachslotTimeOptions = scheduleGameListQuery.recordsets[1][j];
                                    if (eachScheduleGame.GameScheduleID == eachslotTimeOptions.ScheduleID) {
                                        slotTimeArray.push(eachslotTimeOptions.Time != null
                                            ? new Date(eachslotTimeOptions.Time).toISOString().replace(/T/, " ").replace(/\..+/, "")
                                            : null);
                                    }
                                }
                                scheduleGameArray.push({
                                    CTestId: parseInt(eachScheduleGame.CTestId),
                                    CTestName: eachScheduleGame.CTestName,
                                    Version: eachScheduleGame.Version,
                                    GameType: eachScheduleGame.GameType,
                                    Time: eachScheduleGame.Time,
                                    SlotTime: eachScheduleGame.Time != null
                                        ? new Date(eachScheduleGame.Time).toISOString().replace(/T/, " ").replace(/\..+/, "")
                                        : null,
                                    GameScheduleID: parseInt(eachScheduleGame.GameScheduleID),
                                    ScheduleDate: eachScheduleGame.ScheduleDate,
                                    RepeatID: parseInt(eachScheduleGame.RepeatID),
                                    IsDeleted: eachScheduleGame.IsDeleted,
                                    SlotTimeOptions: slotTimeArray,
                                });
                                if ((eachScheduleGame.CTestId == 1 || eachScheduleGame.CTestId == 14) &&
                                    eachScheduleGame.GameType != null) {
                                    scheduleGameArray.CTestName = eachScheduleGame.CTestName.replace("n-", "");
                                }
                                if (eachScheduleGame.RepeatID == 5 ||
                                    eachScheduleGame.RepeatID == 6 ||
                                    eachScheduleGame.RepeatID == 7 ||
                                    eachScheduleGame.RepeatID == 8 ||
                                    eachScheduleGame.RepeatID == 9 ||
                                    eachScheduleGame.RepeatID == 10 ||
                                    eachScheduleGame.RepeatID == 12) {
                                    scheduleGameArray.ScheduleDate = eachScheduleGame.Time;
                                }
                            }
                            ScheduleGameList = scheduleGameArray;
                        }
                    }
                    else {
                        return [2 /*return*/, res.status(500).json({
                                ErrorCode: 2030,
                                ErrorMessage: "An error occured on Store procedure `GetAdminCTestScheduleByUserID_sp`.",
                            })];
                    }
                }
                else {
                    return [2 /*return*/, res.status(500).json({
                            ErrorCode: 2030,
                            ErrorMessage: "An error occured on Store procedure `GetAdminCTestScheduleByUserID_sp`.",
                        })];
                }
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .input("p_UserID", UserID)
                        .input("p_LastFetchedTS", requestData.LastFetchedBatchDate)
                        .output("p_ErrID", mssql_1.default.VarChar(10))
                        .output("p_LastUpdatedTS", mssql_1.default.DateTime)
                        .execute("GetAdminBatchScheduleByUserID_sp")];
            case 12:
                batchScheduleListQuery_1 = _a.sent();
                batchScheduleListOutput = batchScheduleListQuery_1.output;
                if (Object.keys(batchScheduleListOutput).length > 0) {
                    p_ErrID = batchScheduleListOutput.p_ErrID;
                    LastUpdatedBatchDate =
                        batchScheduleListOutput.p_LastUpdatedTS != null
                            ? new Date(batchScheduleListOutput.p_LastUpdatedTS).toISOString().replace(/T/, " ").replace(/\..+/, "")
                            : null;
                    if (p_ErrID == 0) {
                        batchScheduleArray = [];
                        if (batchScheduleListQuery_1.recordsets[0].length > 0) {
                            _loop_1 = function (i, n) {
                                eachBatchSchedule_1 = batchScheduleListQuery_1.recordsets[0][i];
                                var eachslotTimeOptions = void 0;
                                var slotTimeArray = [];
                                for (var j = 0, n_3 = batchScheduleListQuery_1.recordsets[3].length; j < n_3; ++j) {
                                    eachslotTimeOptions = batchScheduleListQuery_1.recordsets[3][j];
                                    if (eachBatchSchedule_1.ScheduleID == eachslotTimeOptions.ScheduleID) {
                                        slotTimeArray.push(eachslotTimeOptions.Time != null
                                            ? new Date(eachslotTimeOptions.Time).toISOString().replace(/T/, " ").replace(/\..+/, "")
                                            : null);
                                    }
                                }
                                //BatchScheduleSurvey_CTest
                                var batchScheduleCtestArray = [];
                                if (batchScheduleListQuery_1.recordsets[1].length > 0) {
                                    var eachBatchCTestSchedule_1;
                                    Object.keys(batchScheduleListQuery_1.recordsets[1]).forEach(function (key) {
                                        eachBatchCTestSchedule_1 = batchScheduleListQuery_1.recordsets[1][key];
                                        if (eachBatchSchedule_1.ScheduleID == eachBatchCTestSchedule_1.ScheduleID) {
                                            batchScheduleCtestArray.push({
                                                BatchScheduleId: parseInt(eachBatchCTestSchedule_1.ScheduleID),
                                                Type: 2,
                                                ID: parseInt(eachBatchCTestSchedule_1.CTestID),
                                                Version: parseInt(eachBatchCTestSchedule_1.Version),
                                                Order: parseInt(eachBatchCTestSchedule_1.Order),
                                                GameType: parseInt(eachBatchCTestSchedule_1.GameType),
                                            });
                                        }
                                    });
                                }
                                //BatchScheduleCustomTime
                                var batchScheduleCustomTimeArray = [];
                                if (batchScheduleListQuery_1.recordsets[3].length > 0) {
                                    var eachBatchCustomTime_1;
                                    Object.keys(batchScheduleListQuery_1.recordsets[3]).forEach(function (key) {
                                        eachBatchCustomTime_1 = batchScheduleListQuery_1.recordsets[3][key];
                                        if (eachBatchSchedule_1.ScheduleID == eachBatchCustomTime_1.ScheduleID) {
                                            batchScheduleCustomTimeArray.push({
                                                BatchScheduleId: parseInt(eachBatchCustomTime_1.ScheduleID),
                                                Time: eachBatchCustomTime_1.Time != null
                                                    ? new Date(eachBatchCustomTime_1.Time).toISOString().replace(/T/, " ").replace(/\..+/, "")
                                                    : null,
                                            });
                                        }
                                    });
                                }
                                batchScheduleArray.push({
                                    BatchScheduleId: parseInt(eachBatchSchedule_1.ScheduleID),
                                    BatchName: eachBatchSchedule_1.BatchName,
                                    ScheduleDate: eachBatchSchedule_1.ScheduleDate != null
                                        ? new Date(eachBatchSchedule_1.ScheduleDate).toISOString().replace(/T/, " ").replace(/\..+/, "")
                                        : null,
                                    Time: eachBatchSchedule_1.Time != null
                                        ? new Date(eachBatchSchedule_1.Time).toISOString().replace(/T/, " ").replace(/\..+/, "")
                                        : null,
                                    RepeatID: parseInt(eachBatchSchedule_1.RepeatID),
                                    IsDeleted: eachBatchSchedule_1.IsDeleted,
                                    IconBlob: eachBatchSchedule_1.IconBlob != null ? Buffer.from(eachBatchSchedule_1.IconBlob).toString("base64") : null,
                                    IconBlobString: eachBatchSchedule_1.IconBlob != null ? Buffer.from(eachBatchSchedule_1.IconBlob).toString("base64") : null,
                                    SlotTimeOptions: slotTimeArray,
                                    BatchScheduleSurvey_CTest: batchScheduleCtestArray,
                                    BatchScheduleCustomTime: batchScheduleCustomTimeArray,
                                });
                                if (eachBatchSchedule_1.RepeatID == 5 ||
                                    eachBatchSchedule_1.RepeatID == 6 ||
                                    eachBatchSchedule_1.RepeatID == 7 ||
                                    eachBatchSchedule_1.RepeatID == 8 ||
                                    eachBatchSchedule_1.RepeatID == 9 ||
                                    eachBatchSchedule_1.RepeatID == 10 ||
                                    eachBatchSchedule_1.RepeatID == 12) {
                                    batchScheduleArray.ScheduleDate = eachBatchSchedule_1.Time;
                                }
                            };
                            for (i = 0, n = batchScheduleListQuery_1.recordsets[0].length; i < n; ++i) {
                                _loop_1(i, n);
                            }
                            BatchScheduleList = batchScheduleArray;
                        }
                    }
                    else {
                        return [2 /*return*/, res.status(500).json({
                                ErrorCode: 2030,
                                ErrorMessage: "An error occured on Store procedure `GetAdminBatchScheduleByUserID_sp`.",
                            })];
                    }
                }
                else {
                    return [2 /*return*/, res.status(500).json({
                            ErrorCode: 2030,
                            ErrorMessage: "An error occured on Store procedure `GetAdminBatchScheduleByUserID_sp`.",
                        })];
                }
                return [3 /*break*/, 14];
            case 13: return [2 /*return*/, res.status(500).json({
                    ErrorCode: 2030,
                    ErrorMessage: "Specified User ID doesnot exists.",
                })];
            case 14: return [2 /*return*/, res.status(200).json({
                    ContactNo: ContactNo,
                    PersonalHelpline: PersonalHelpline,
                    JewelsTrailsASettings: JewelsTrailsASettings,
                    JewelsTrailsBSettings: JewelsTrailsBSettings,
                    ReminderClearInterval: ReminderClearInterval,
                    CognitionOffList: CognitionOffList,
                    CognitionIconList: CognitionIconList,
                    SurveyIconList: SurveyIconList,
                    CognitionVersionList: CognitionVersionList,
                    ScheduleSurveyList: ScheduleSurveyList,
                    BatchScheduleList: BatchScheduleList,
                    ScheduleGameList: ScheduleGameList,
                    LastUpdatedSurveyDate: LastUpdatedSurveyDate,
                    LastUpdatedGameDate: LastUpdatedGameDate,
                    LastUpdatedBatchDate: LastUpdatedBatchDate,
                    ErrorCode: 0,
                    ErrorMessage: "Survey And Game Schedule Detail.",
                })];
        }
    });
}); });
// Route: /GetDistractionSurveys // USES SQL
exports.LegacyAPI.post("/GetDistractionSurveys", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var requestData, SurveysList, UserID, CTestId, resultQuery, AdminID, surveysQuery;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                requestData = req.body;
                SurveysList = [];
                UserID = requestData.UserId;
                if (!UserID || !Number.isInteger(Number(UserID)) || UserID == 0) {
                    return [2 /*return*/, res.status(422).json({
                            ErrorCode: 2031,
                            ErrorMessage: "Specify valid User Id.",
                        })];
                }
                CTestId = requestData.CTestId;
                if (!CTestId || !Number.isInteger(CTestId) || CTestId == 0) {
                    return [2 /*return*/, res.status(422).json({
                            ErrorCode: 2031,
                            ErrorMessage: "Specify valid CTest Id.",
                        })];
                }
                return [4 /*yield*/, app_1.SQL.request().query("SELECT AdminID FROM Users WHERE IsDeleted = 0 AND UserID = " + UserID)];
            case 1:
                resultQuery = _a.sent();
                if (!(resultQuery.recordset.length > 0)) return [3 /*break*/, 3];
                AdminID = resultQuery.recordset[0].AdminID;
                return [4 /*yield*/, app_1.SQL
                        .request()
                        .query("SELECT a_cs.SurveyID SurveyId FROM Admin_CTestSurveySettings as a_cs JOIN Survey as s_y " +
                        "ON a_cs.SurveyID = s_y.SurveyID WHERE s_y.IsDeleted = 0 " +
                        " AND a_cs.AdminID = " +
                        AdminID +
                        " AND a_cs.CTestID = " +
                        CTestId)];
            case 2:
                surveysQuery = _a.sent();
                if (surveysQuery.recordset.length > 0) {
                    SurveysList = surveysQuery.recordset;
                }
                _a.label = 3;
            case 3: return [2 /*return*/, res.status(200).json({
                    Surveys: SurveysList,
                    ErrorCode: 0,
                    ErrorMessage: "Distraction Surveys Detail.",
                })];
        }
    });
}); });
// Route: /GetSurveys // USES SQL
exports.LegacyAPI.post("/GetSurveys", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var requestData, UserID, resultQuery2;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                requestData = req.body;
                UserID = requestData.UserID;
                if (!UserID || !Number.isInteger(Number(UserID)) || UserID == 0) {
                    return [2 /*return*/, res.status(422).json({
                            ErrorCode: 2031,
                            ErrorMessage: "Specify valid User Id.",
                        })];
                }
                return [4 /*yield*/, app_1.SQL.request().query("\n        SELECT \n            SurveyID, \n            SurveyName, \n            Instructions AS Instruction, \n            ISNULL(Language, 'en') AS LanguageCode, \n            IsDeleted, \n            (\n                SELECT \n                    QuestionID AS QuestionId,\n                    QuestionText,\n                    (CASE \n                        WHEN AnswerType = 1 THEN 'LikertResponse'\n                        WHEN AnswerType = 2 THEN 'ScrollWheels'\n                        WHEN AnswerType = 3 THEN 'YesNO'\n                        WHEN AnswerType = 4 THEN 'Clock'\n                        WHEN AnswerType = 5 THEN 'Years'\n                        WHEN AnswerType = 6 THEN 'Months'\n                        WHEN AnswerType = 7 THEN 'Days'\n                        WHEN AnswerType = 8 THEN 'Textbox' \n                    END) AS AnswerType,\n                    IsDeleted,\n                    (\n                        SELECT \n                            OptionText\n                        FROM SurveyQuestionOptions\n                        WHERE SurveyQuestions.QuestionID = SurveyQuestionOptions.QuestionID\n                        FOR JSON AUTO, INCLUDE_NULL_VALUES\n                    ) AS QuestionOptions,\n                    CAST(0 AS BIT) AS EnableCustomPopup,\n                    Threshold AS ThresholdId,\n                    Operator AS OperatorId,\n                    Message AS CustomPopupMessage\n                FROM SurveyQuestions\n                WHERE Survey.SurveyID = SurveyQuestions.SurveyID\n                FOR JSON AUTO, INCLUDE_NULL_VALUES\n            ) AS Questions\n        FROM Survey \n        WHERE IsDeleted = 0\n        AND AdminID IN (\n            SELECT \n                AdminID \n            FROM Users \n            WHERE IsDeleted = 0 \n                AND UserID = " + UserID + "\n        )\n        FOR JSON AUTO, INCLUDE_NULL_VALUES\n    ;")];
            case 1:
                resultQuery2 = _a.sent();
                return [2 /*return*/, res.status(200).json({
                        ErrorCode: 0,
                        ErrorMessage: "Get surveys detail.",
                        Survey: resultQuery2.recordset[0],
                        LastUpdatedDate: new Date().toISOString().replace(/T/, " ").replace(/\..+/, ""),
                    })];
        }
    });
}); });
// Route: /SaveUserSurvey
exports.LegacyAPI.post("/SaveUserSurvey", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var data, activityEvent, _a, out, res2, notificationEvent, out_1;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                data = req.body // TODO: StatusType field?
                ;
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ survey_id: data.SurveyID }))];
            case 1:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {},
                    _a.temporal_slices = data.QuestAndAnsList.map(function (x) {
                        var temporal_slice = {
                            item: x.Question,
                            value: x.Answer,
                            type: "valid",
                            duration: (x.TimeTaken * 1000),
                            level: null,
                        };
                        // Adjust the Likert scaled values to numbers.
                        if (["Not at all", "12:00AM - 06:00AM", "0-3"].indexOf(temporal_slice.value) >= 0) {
                            temporal_slice.value = 0;
                        }
                        else if (["Several Times", "06:00AM - 12:00PM", "3-6"].indexOf(temporal_slice.value) >= 0) {
                            temporal_slice.value = 1;
                        }
                        else if (["More than Half the Time", "12:00PM - 06:00PM", "6-9"].indexOf(temporal_slice.value) >= 0) {
                            temporal_slice.value = 2;
                        }
                        else if (["Nearly All the Time", "06:00PM - 12:00AM", ">9"].indexOf(temporal_slice.value) >= 0) {
                            temporal_slice.value = 3;
                        }
                        return temporal_slice;
                    }),
                    _a);
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 2:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 5];
                return [4 /*yield*/, app_1.SQL.query("\n            SELECT (CASE DeviceType \n                WHEN 1 THEN 'iOS'\n                WHEN 2 THEN 'Android'\n            END) AS device_type \n            FROM LAMP.dbo.UserDevices\n            WHERE UserID = " + data.UserID + "\n        ;")];
            case 3:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "Survey",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 4:
                out_1 = _b.sent();
                console.dir(out_1.filter(function (x) { return !!x.error; }));
                _b.label = 5;
            case 5: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /SaveUserHealthKit
exports.LegacyAPI.post("/SaveUserHealthKit", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var ParamIDLookup, data, sensorEvents, out;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                ParamIDLookup = {
                    Height: "lamp.height",
                    Weight: "lamp.weight",
                    HeartRate: "lamp.heart_rate",
                    BloodPressure: "lamp.blood_pressure",
                    RespiratoryRate: "lamp.respiratory_rate",
                    Sleep: "lamp.sleep",
                    Steps: "lamp.steps",
                    Flights: "lamp.flights",
                    Segment: "lamp.segment",
                    Distance: "lamp.distance",
                };
                data = req.body;
                sensorEvents = Object.entries(data)
                    .filter(function (_a) {
                    var _b = __read(_a, 2), key = _b[0], value = _b[1];
                    var _c, _d;
                    return Object.keys(ParamIDLookup).includes(key) && (_d = (_c = value) === null || _c === void 0 ? void 0 : _c.length, (_d !== null && _d !== void 0 ? _d : 0 > 0));
                })
                    .map(function (_a) {
                    var _b = __read(_a, 2), key = _b[0], value = _b[1];
                    var _c, _d;
                    return ({
                        "#parent": req.AuthUser.StudyId,
                        timestamp: new Date().getTime(),
                        sensor: ParamIDLookup[key],
                        data: {
                            value: (_c = value.split(" ")[0], (_c !== null && _c !== void 0 ? _c : "")),
                            units: (_d = value.split(" ")[1], (_d !== null && _d !== void 0 ? _d : "")),
                        },
                    });
                });
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: sensorEvents })];
            case 1:
                out = _a.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                return [2 /*return*/, res.status(200).json({
                        ErrorCode: 0,
                        ErrorMessage: "API Method Auto-Forwarded",
                    })];
        }
    });
}); });
// Route: /SaveUserHealthKitV2
exports.LegacyAPI.post("/SaveUserHealthKitV2", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var ParamIDLookup, data, sensorEvents, out;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                ParamIDLookup = {
                    Height: "lamp.height",
                    Weight: "lamp.weight",
                    HeartRate: "lamp.heart_rate",
                    BloodPressure: "lamp.blood_pressure",
                    RespiratoryRate: "lamp.respiratory_rate",
                    Sleep: "lamp.sleep",
                    Steps: "lamp.steps",
                    Flights: "lamp.flights",
                    Segment: "lamp.segment",
                    Distance: "lamp.distance",
                };
                data = req.body;
                sensorEvents = data.HealthKitParams.map(function (param) {
                    var _a, _b;
                    return ({
                        "#parent": req.AuthUser.StudyId,
                        timestamp: new Date().getTime(),
                        sensor: ParamIDLookup[param.ParamID],
                        data: {
                            value: (_a = param.Value.split(" ")[0], (_a !== null && _a !== void 0 ? _a : "")),
                            units: (_b = param.Value.split(" ")[1], (_b !== null && _b !== void 0 ? _b : "")),
                        },
                    });
                });
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: sensorEvents })];
            case 1:
                out = _a.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                return [2 /*return*/, res.status(200).json({
                        ErrorCode: 0,
                        ErrorMessage: "API Method Auto-Forwarded",
                    })];
        }
    });
}); });
// Route: /SaveLocation
exports.LegacyAPI.post("/SaveLocation", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var toLAMP, data, x, sensorEvent, out;
    var _a, _b, _c, _d;
    return __generator(this, function (_e) {
        switch (_e.label) {
            case 0:
                toLAMP = function (value) {
                    if (!value)
                        return [];
                    var matches = (app_1.Decrypt(value) || value).toLowerCase().match(/(?:i am )([ \S\/]+)(alone|in [ \S\/]*|with [ \S\/]*)/) || [];
                    return [
                        {
                            home: "home",
                            "at home": "home",
                            "in school/class": "school",
                            "at work": "work",
                            "in clinic/hospital": "hospital",
                            outside: "outside",
                            "shopping/dining": "shopping",
                            "in bus/train/car": "transit",
                        }[(matches[1] || " ").slice(0, -1)],
                        {
                            alone: "alone",
                            "with friends": "friends",
                            "with family": "family",
                            "with peers": "peers",
                            "in crowd": "crowd",
                        }[matches[2] || ""],
                    ];
                };
                data = req.body;
                x = toLAMP(data.LocationName);
                sensorEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date().getTime(),
                    sensor: "lamp.gps.contextual",
                    data: {
                        latitude: parseFloat((_b = app_1.Decrypt((_a = data.Latitude, (_a !== null && _a !== void 0 ? _a : ""))), (_b !== null && _b !== void 0 ? _b : data.Latitude))),
                        longitude: parseFloat((_d = app_1.Decrypt((_c = data.Longitude, (_c !== null && _c !== void 0 ? _c : ""))), (_d !== null && _d !== void 0 ? _d : data.Longitude))),
                        accuracy: -1,
                        context: {
                            environment: x[0] || null,
                            social: x[1] || null,
                        },
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [sensorEvent] })];
            case 1:
                out = _e.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                return [2 /*return*/, res.status(200).json({
                        ErrorCode: 0,
                        ErrorMessage: "API Method Auto-Forwarded",
                    })];
        }
    });
}); });
// Route: /SaveHelpCall
exports.LegacyAPI.post("/SaveHelpCall", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        return [2 /*return*/, res.status(200).json({
                ErrorCode: 0,
                ErrorMessage: "Disabled",
            })];
    });
}); });
// Route: /SaveNBackGame
exports.LegacyAPI.post("/SaveNBackGame", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, settingID, activityEvent, _a, out, res2, notificationEvent, out_2;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 1;
                data = req.body // TODO: StatusType field?
                ;
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        point: data.Point,
                        score: data.Score,
                        version: data.Version,
                        correct_answers: data.CorrectAnswers,
                        wrong_answers: data.WrongAnswers,
                    },
                    _a.temporal_slices = [],
                    _a);
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 6];
                return [4 /*yield*/, app_1.SQL.query("\n             SELECT (CASE DeviceType \n                 WHEN 1 THEN 'iOS'\n                 WHEN 2 THEN 'Android'\n             END) AS device_type \n             FROM LAMP.dbo.UserDevices\n             WHERE UserID = " + data.UserID + "\n         ;")];
            case 4:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "NBack",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 5:
                out_2 = _b.sent();
                console.dir(out_2.filter(function (x) { return !!x.error; }));
                _b.label = 6;
            case 6: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /SaveTrailsBGame
exports.LegacyAPI.post("/SaveTrailsBGame", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, settingID, activityEvent, _a, out, res2, notificationEvent, out_3;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 2;
                data = req.body // TODO: StatusType field?
                ;
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        point: data.Point,
                        score: data.Score,
                        version: data.Version,
                        total_attempts: data.TotalAttempts,
                    },
                    _a.temporal_slices = data.RoutesList.reduce(function (prev, curr, idx) {
                        return prev.concat(curr.Routes.map(function (x) { return ({
                            item: x.Alphabet,
                            value: x.Status,
                            type: null,
                            duration: parseFloat(x.TimeTaken) * 1000,
                            level: idx + 1,
                        }); }));
                    }, []),
                    _a);
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 6];
                return [4 /*yield*/, app_1.SQL.query("\n             SELECT (CASE DeviceType \n                 WHEN 1 THEN 'iOS'\n                 WHEN 2 THEN 'Android'\n             END) AS device_type \n             FROM LAMP.dbo.UserDevices\n             WHERE UserID = " + data.UserID + "\n         ;")];
            case 4:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "TrailsB",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 5:
                out_3 = _b.sent();
                console.dir(out_3.filter(function (x) { return !!x.error; }));
                _b.label = 6;
            case 6: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /SaveSpatialSpanGame
exports.LegacyAPI.post("/SaveSpatialSpanGame", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, settingID, activityEvent, _a, out, res2, notificationEvent, out_4;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 3 // [3, 4] = [Backward, Forward] variants
                ;
                data = req.body // TODO: StatusType field?
                ;
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        type: data.Type,
                        point: data.Point,
                        score: data.Score,
                        correct_answers: data.CorrectAnswers,
                        wrong_answers: data.WrongAnswers,
                    },
                    _a.temporal_slices = data.BoxList.reduce(function (prev, curr, idx) {
                        return prev.concat(curr.Boxes.map(function (x) { return ({
                            item: x.GameIndex,
                            value: idx + 1,
                            type: x.Status,
                            duration: parseFloat(x.TimeTaken) * 1000,
                            level: x.Level,
                        }); }));
                    }, []),
                    _a);
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 6];
                return [4 /*yield*/, app_1.SQL.query("\n             SELECT (CASE DeviceType \n                 WHEN 1 THEN 'iOS'\n                 WHEN 2 THEN 'Android'\n             END) AS device_type \n             FROM LAMP.dbo.UserDevices\n             WHERE UserID = " + data.UserID + "\n         ;")];
            case 4:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "SpatialSpan",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 5:
                out_4 = _b.sent();
                console.dir(out_4.filter(function (x) { return !!x.error; }));
                _b.label = 6;
            case 6: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /SaveSimpleMemoryGame
exports.LegacyAPI.post("/SaveSimpleMemoryGame", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, settingID, activityEvent, _a, out, res2, notificationEvent, out_5;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 5;
                data = req.body // TODO: StatusType field?
                ;
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        point: data.Point,
                        score: data.Score,
                        version: data.Version,
                        correct_answers: data.CorrectAnswers,
                        wrong_answers: data.WrongAnswers,
                    },
                    _a.temporal_slices = [],
                    _a);
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 6];
                return [4 /*yield*/, app_1.SQL.query("\n             SELECT (CASE DeviceType \n                 WHEN 1 THEN 'iOS'\n                 WHEN 2 THEN 'Android'\n             END) AS device_type \n             FROM LAMP.dbo.UserDevices\n             WHERE UserID = " + data.UserID + "\n         ;")];
            case 4:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "SimpleMemory",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 5:
                out_5 = _b.sent();
                console.dir(out_5.filter(function (x) { return !!x.error; }));
                _b.label = 6;
            case 6: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /SaveSerial7Game
exports.LegacyAPI.post("/SaveSerial7Game", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, settingID, activityEvent, _a, out, res2, notificationEvent, out_6;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 6;
                data = req.body // TODO: StatusType field?
                ;
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        point: data.Point,
                        score: data.Score,
                        version: data.Version,
                        total_questions: data.TotalQuestions,
                        total_attempts: data.TotalAttempts,
                    },
                    _a.temporal_slices = [],
                    _a);
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 6];
                return [4 /*yield*/, app_1.SQL.query("\n             SELECT (CASE DeviceType \n                 WHEN 1 THEN 'iOS'\n                 WHEN 2 THEN 'Android'\n             END) AS device_type \n             FROM LAMP.dbo.UserDevices\n             WHERE UserID = " + data.UserID + "\n         ;")];
            case 4:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "Serial7s",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 5:
                out_6 = _b.sent();
                console.dir(out_6.filter(function (x) { return !!x.error; }));
                _b.label = 6;
            case 6: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /SaveCatAndDogGame
exports.LegacyAPI.post("/SaveCatAndDogGame", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, settingID, activityEvent, _a, out, res2, notificationEvent, out_7;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 7;
                data = req.body // TODO: StatusType field?
                ;
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        point: data.Point,
                        total_questions: data.TotalQuestions,
                        correct_answers: data.CorrectAnswers,
                        wrong_answers: data.WrongAnswers,
                    },
                    _a.temporal_slices = [],
                    _a);
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 6];
                return [4 /*yield*/, app_1.SQL.query("\n             SELECT (CASE DeviceType \n                 WHEN 1 THEN 'iOS'\n                 WHEN 2 THEN 'Android'\n             END) AS device_type \n             FROM LAMP.dbo.UserDevices\n             WHERE UserID = " + data.UserID + "\n         ;")];
            case 4:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "CatAndDog",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 5:
                out_7 = _b.sent();
                console.dir(out_7.filter(function (x) { return !!x.error; }));
                _b.label = 6;
            case 6: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /Save3DFigureGame
exports.LegacyAPI.post("/Save3DFigureGame", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, new_filename, settingID, activityEvent, _a, out, res2, notificationEvent, out_8;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 8;
                data = req.body // TODO: StatusType field?
                ;
                new_filename = req.AuthUser.UserID + "_" + uuid_1.v4() + ".png";
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        point: data.Point,
                        drawn_fig_file_name: new_filename,
                        game_name: data.GameName,
                    },
                    _a.temporal_slices = [],
                    _a);
                app_1.S3.upload({
                    Bucket: app_1.AWSBucketName,
                    Key: "Games/User3DFigures/" + new_filename,
                    Body: data.DrawnFig,
                    ACL: "public-read",
                    ContentEncoding: "base64",
                    ContentType: "image/png",
                }, function (err, data) {
                    console.dir({ data: data, err: err });
                });
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 6];
                return [4 /*yield*/, app_1.SQL.query("\n            SELECT (CASE DeviceType \n                WHEN 1 THEN 'iOS'\n                WHEN 2 THEN 'Android'\n            END) AS device_type \n            FROM LAMP.dbo.UserDevices\n            WHERE UserID = " + data.UserID + "\n        ;")];
            case 4:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "3DFigure",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 5:
                out_8 = _b.sent();
                console.dir(out_8.filter(function (x) { return !!x.error; }));
                _b.label = 6;
            case 6: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /SaveVisualAssociationGame
exports.LegacyAPI.post("/SaveVisualAssociationGame", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, settingID, activityEvent, _a, out, res2, notificationEvent, out_9;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 9;
                data = req.body // TODO: StatusType field?
                ;
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        point: data.Point,
                        score: data.Score,
                        version: data.Version,
                        total_questions: data.TotalQuestions,
                        correct_answers: data.CorrectAnswers,
                        wrong_answers: data.WrongAnswers,
                    },
                    _a.temporal_slices = [],
                    _a);
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 6];
                return [4 /*yield*/, app_1.SQL.query("\n             SELECT (CASE DeviceType \n                 WHEN 1 THEN 'iOS'\n                 WHEN 2 THEN 'Android'\n             END) AS device_type \n             FROM LAMP.dbo.UserDevices\n             WHERE UserID = " + data.UserID + "\n         ;")];
            case 4:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "VisualAssociation",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 5:
                out_9 = _b.sent();
                console.dir(out_9.filter(function (x) { return !!x.error; }));
                _b.label = 6;
            case 6: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /SaveDigitSpanGame
exports.LegacyAPI.post("/SaveDigitSpanGame", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, settingID, activityEvent, _a, out, res2, notificationEvent, out_10;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 10 // [10, 13] = [Backward, Forward] variants
                ;
                data = req.body // TODO: StatusType field?
                ;
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        type: data.Type,
                        point: data.Point,
                        score: data.Score,
                        correct_answers: data.CorrectAnswers,
                        wrong_answers: data.WrongAnswers,
                    },
                    _a.temporal_slices = [],
                    _a);
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 6];
                return [4 /*yield*/, app_1.SQL.query("\n             SELECT (CASE DeviceType \n                 WHEN 1 THEN 'iOS'\n                 WHEN 2 THEN 'Android'\n             END) AS device_type \n             FROM LAMP.dbo.UserDevices\n             WHERE UserID = " + data.UserID + "\n         ;")];
            case 4:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "DigitSpan",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 5:
                out_10 = _b.sent();
                console.dir(out_10.filter(function (x) { return !!x.error; }));
                _b.label = 6;
            case 6: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /SaveCatAndDogNewGame
exports.LegacyAPI.post("/SaveCatAndDogNewGame", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, settingID, activityEvent, _a, out, res2, notificationEvent, out_11;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 11;
                data = req.body // TODO: StatusType field?
                ;
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        point: data.Point,
                        score: data.Score,
                        correct_answers: data.CorrectAnswers,
                        wrong_answers: data.WrongAnswers,
                    },
                    _a.temporal_slices = data.GameLevelDetailList.map(function (x) { return ({
                        item: null,
                        value: x.CorrectAnswer,
                        type: x.WrongAnswer,
                        duration: parseFloat(x.TimeTaken) * 1000,
                        level: null,
                    }); }),
                    _a);
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 6];
                return [4 /*yield*/, app_1.SQL.query("\n             SELECT (CASE DeviceType \n                 WHEN 1 THEN 'iOS'\n                 WHEN 2 THEN 'Android'\n             END) AS device_type \n             FROM LAMP.dbo.UserDevices\n             WHERE UserID = " + data.UserID + "\n         ;")];
            case 4:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "CatAndDogNew",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 5:
                out_11 = _b.sent();
                console.dir(out_11.filter(function (x) { return !!x.error; }));
                _b.label = 6;
            case 6: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /SaveTemporalOrderGame
exports.LegacyAPI.post("/SaveTemporalOrderGame", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, settingID, activityEvent, _a, out, res2, notificationEvent, out_12;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 12;
                data = req.body // TODO: StatusType field?
                ;
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        point: data.Point,
                        score: data.Score,
                        version: data.Version,
                        correct_answers: data.CorrectAnswers,
                        wrong_answers: data.WrongAnswers,
                    },
                    _a.temporal_slices = [],
                    _a);
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 6];
                return [4 /*yield*/, app_1.SQL.query("\n             SELECT (CASE DeviceType \n                 WHEN 1 THEN 'iOS'\n                 WHEN 2 THEN 'Android'\n             END) AS device_type \n             FROM LAMP.dbo.UserDevices\n             WHERE UserID = " + data.UserID + "\n         ;")];
            case 4:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "TemporalOrder",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 5:
                out_12 = _b.sent();
                console.dir(out_12.filter(function (x) { return !!x.error; }));
                _b.label = 6;
            case 6: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /SaveNBackGameNewGame
exports.LegacyAPI.post("/SaveNBackGameNewGame", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, settingID, activityEvent, _a, out, res2, notificationEvent, out_13;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 14;
                data = req.body // TODO: StatusType field?
                ;
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        point: data.Point,
                        score: data.Score,
                        total_questions: data.TotalQuestions,
                        correct_answers: data.CorrectAnswers,
                        wrong_answers: data.WrongAnswers,
                    },
                    _a.temporal_slices = [],
                    _a);
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 6];
                return [4 /*yield*/, app_1.SQL.query("\n             SELECT (CASE DeviceType \n                 WHEN 1 THEN 'iOS'\n                 WHEN 2 THEN 'Android'\n             END) AS device_type \n             FROM LAMP.dbo.UserDevices\n             WHERE UserID = " + data.UserID + "\n         ;")];
            case 4:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "NBackNew",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 5:
                out_13 = _b.sent();
                console.dir(out_13.filter(function (x) { return !!x.error; }));
                _b.label = 6;
            case 6: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /SaveTrailsBGameNew
exports.LegacyAPI.post("/SaveTrailsBGameNew", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, settingID, activityEvent, _a, out, res2, notificationEvent, out_14;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 15;
                data = req.body // TODO: StatusType field?
                ;
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        point: data.Point,
                        score: data.Score,
                        version: data.Version,
                        total_attempts: data.TotalAttempts,
                    },
                    _a.temporal_slices = data.RoutesList.reduce(function (prev, curr, idx) {
                        return prev.concat(curr.Routes.map(function (x) { return ({
                            item: x.Alphabet,
                            value: x.Status,
                            type: null,
                            duration: parseFloat(x.TimeTaken) * 1000,
                            level: idx + 1,
                        }); }));
                    }, []),
                    _a);
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 6];
                return [4 /*yield*/, app_1.SQL.query("\n             SELECT (CASE DeviceType \n                 WHEN 1 THEN 'iOS'\n                 WHEN 2 THEN 'Android'\n             END) AS device_type \n             FROM LAMP.dbo.UserDevices\n             WHERE UserID = " + data.UserID + "\n         ;")];
            case 4:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "TrailsBNew",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 5:
                out_14 = _b.sent();
                console.dir(out_14.filter(function (x) { return !!x.error; }));
                _b.label = 6;
            case 6: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /SaveTrailsBDotTouchGame
exports.LegacyAPI.post("/SaveTrailsBDotTouchGame", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, settingID, activityEvent, _a, out, res2, notificationEvent, out_15;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 16;
                data = req.body // TODO: StatusType field?
                ;
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        point: data.Point,
                        score: data.Score,
                        total_attempts: data.TotalAttempts,
                    },
                    _a.temporal_slices = data.RoutesList.reduce(function (prev, curr, idx) {
                        return prev.concat(curr.Routes.map(function (x) { return ({
                            item: x.Alphabet,
                            value: x.Status,
                            type: null,
                            duration: parseFloat(x.TimeTaken) * 1000,
                            level: idx + 1,
                        }); }));
                    }, []),
                    _a);
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 6];
                return [4 /*yield*/, app_1.SQL.query("\n             SELECT (CASE DeviceType \n                 WHEN 1 THEN 'iOS'\n                 WHEN 2 THEN 'Android'\n             END) AS device_type \n             FROM LAMP.dbo.UserDevices\n             WHERE UserID = " + data.UserID + "\n         ;")];
            case 4:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "TrailsBDotTouch",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 5:
                out_15 = _b.sent();
                console.dir(out_15.filter(function (x) { return !!x.error; }));
                _b.label = 6;
            case 6: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /SaveJewelsTrailsAGame
exports.LegacyAPI.post("/SaveJewelsTrailsAGame", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, settingID, activityEvent, _a, out, res2, notificationEvent, out_16;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 17;
                data = req.body // TODO: StatusType field?
                ;
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        point: data.Point,
                        score: data.Score,
                        total_attempts: data.TotalAttempts,
                        total_jewels_collected: data.TotalJewelsCollected,
                        total_bonus_collected: data.TotalBonusCollected,
                    },
                    _a.temporal_slices = data.RoutesList.reduce(function (prev, curr, idx) {
                        return prev.concat(curr.Routes.map(function (x) { return ({
                            item: x.Alphabet,
                            value: x.Status,
                            type: null,
                            duration: parseFloat(x.TimeTaken) * 1000,
                            level: idx + 1,
                        }); }));
                    }, []),
                    _a);
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 6];
                return [4 /*yield*/, app_1.SQL.query("\n             SELECT (CASE DeviceType \n                 WHEN 1 THEN 'iOS'\n                 WHEN 2 THEN 'Android'\n             END) AS device_type \n             FROM LAMP.dbo.UserDevices\n             WHERE UserID = " + data.UserID + "\n         ;")];
            case 4:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "JewelsTrailsA",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 5:
                out_16 = _b.sent();
                console.dir(out_16.filter(function (x) { return !!x.error; }));
                _b.label = 6;
            case 6: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /SaveJewelsTrailsBGame
exports.LegacyAPI.post("/SaveJewelsTrailsBGame", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, settingID, activityEvent, _a, out, res2, notificationEvent, out_17;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 18;
                data = req.body // TODO: StatusType field?
                ;
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        point: data.Point,
                        score: data.Score,
                        total_attempts: data.TotalAttempts,
                        total_jewels_collected: data.TotalJewelsCollected,
                        total_bonus_collected: data.TotalBonusCollected,
                    },
                    _a.temporal_slices = data.RoutesList.reduce(function (prev, curr, idx) {
                        return prev.concat(curr.Routes.map(function (x) { return ({
                            item: x.Alphabet,
                            value: x.Status,
                            type: null,
                            duration: parseFloat(x.TimeTaken) * 1000,
                            level: idx + 1,
                        }); }));
                    }, []),
                    _a);
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 6];
                return [4 /*yield*/, app_1.SQL.query("\n             SELECT (CASE DeviceType \n                 WHEN 1 THEN 'iOS'\n                 WHEN 2 THEN 'Android'\n             END) AS device_type \n             FROM LAMP.dbo.UserDevices\n             WHERE UserID = " + data.UserID + "\n         ;")];
            case 4:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "JewelsTrailsB",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 5:
                out_17 = _b.sent();
                console.dir(out_17.filter(function (x) { return !!x.error; }));
                _b.label = 6;
            case 6: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /SaveScratchImageGame
exports.LegacyAPI.post("/SaveScratchImageGame", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, settingID, new_filename, activityEvent, _a, out, res2, notificationEvent, out_18;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 19;
                data = req.body // TODO: StatusType field?
                ;
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                new_filename = req.AuthUser.UserID + "_" + uuid_1.v4() + ".png";
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.EndTime).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        point: data.Point,
                        scratch_file_name: new_filename,
                        game_name: data.GameName,
                    },
                    _a.temporal_slices = [],
                    _a);
                app_1.S3.upload({
                    Bucket: app_1.AWSBucketName,
                    Key: "Games/UserScratchImages/" + new_filename,
                    Body: data.DrawnImage,
                    ACL: "public-read",
                    ContentEncoding: "base64",
                    ContentType: "image/png",
                }, function (err, data) {
                    console.dir({ data: data, err: err });
                });
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                if (!!!data.IsNotificationGame) return [3 /*break*/, 6];
                return [4 /*yield*/, app_1.SQL.query("\n             SELECT (CASE DeviceType \n                 WHEN 1 THEN 'iOS'\n                 WHEN 2 THEN 'Android'\n             END) AS device_type \n             FROM LAMP.dbo.UserDevices\n             WHERE UserID = " + data.UserID + "\n         ;")];
            case 4:
                res2 = _b.sent();
                notificationEvent = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    sensor: "lamp.analytics",
                    data: {
                        device_type: res2.recordset.length > 0 ? res2.recordset[0].device_type : "Unknown",
                        event_type: "notification",
                        category: "ScratchImage",
                    },
                };
                return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: [notificationEvent] })];
            case 5:
                out_18 = _b.sent();
                console.dir(out_18.filter(function (x) { return !!x.error; }));
                _b.label = 6;
            case 6: return [2 /*return*/, res.status(200).json({
                    ErrorCode: 0,
                    ErrorMessage: "API Method Auto-Forwarded",
                })];
        }
    });
}); });
// Route: /SaveSpinWheelGame
exports.LegacyAPI.post("/SaveSpinWheelGame", [_authorize], function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var LegacyCTestID, data, settingID, activityEvent, _a, out;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                LegacyCTestID = 20;
                data = req.body // TODO: StatusType field?
                ;
                return [4 /*yield*/, app_1.SQL.query("\n        SELECT \n            AdminCTestSettingID AS id\n          FROM Admin_CTestSettings\n          WHERE Admin_CTestSettings.AdminID IN (\n            SELECT AdminID\n            FROM Users\n            WHERE Users.UserID = " + data.UserID + "\n          ) AND Admin_CTestSettings.CTestID = " + LegacyCTestID + "\n    ;")];
            case 1:
                settingID = (_b.sent()).recordset[0]["id"];
                _a = {
                    "#parent": req.AuthUser.StudyId,
                    timestamp: new Date(data.StartTime).getTime(),
                    duration: new Date(data.GameDate).getTime() - new Date(data.StartTime).getTime()
                };
                return [4 /*yield*/, _lookup_migrator_id(migrate_1.Activity_pack_id({ ctest_id: settingID }))];
            case 2:
                activityEvent = (_a.activity = _b.sent(),
                    _a.static_data = {
                        collected_stars: data.CollectedStars,
                        day_streak: data.DayStreak,
                        streak_spin: data.StrakSpin,
                    },
                    _a.temporal_slices = [],
                    _a);
                return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: [activityEvent] })];
            case 3:
                out = _b.sent();
                console.dir(out.filter(function (x) { return !!x.error; }));
                return [2 /*return*/, res.status(200).json({
                        ErrorCode: 0,
                        ErrorMessage: "API Method Auto-Forwarded",
                    })];
        }
    });
}); });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoicm91dGUuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi9zcmMvdXRpbHMvbGVnYWN5L3JvdXRlLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUFBLDZEQUE2RDtBQUM3RCxpQ0FBOEU7QUFDOUUsb0RBQW1GO0FBQ25GLGdEQUF1QjtBQUN2QixtQ0FBbUQ7QUFDbkQsNkJBQW1DO0FBQ25DLGlDQUF1QztBQUN2QyxJQUFNLElBQUksR0FBRyx1QkFBYyxDQUFDLGtDQUFrQyxFQUFFLEVBQUUsQ0FBQyxDQUFBLENBQUMsZUFBZTtBQUV0RSxRQUFBLFNBQVMsR0FBRyxnQkFBTSxFQUFFLENBQUE7QUFFakMscURBQXFEO0FBQ3JELElBQU0sVUFBVSxHQUFHLFVBQU8sR0FBWSxFQUFFLEdBQWEsRUFBRSxJQUFTOzs7Ozs7Z0JBQ3hELEtBQUssR0FBRyxNQUFDLEdBQUcsQ0FBQyxPQUFPLENBQUMsZUFBZSxDQUFDLHVDQUFJLEVBQUUsRUFBQyxDQUFDLEtBQUssQ0FBQyxHQUFHLENBQUMsQ0FBQTtnQkFDN0QsSUFBSSxDQUFDLEdBQUcsQ0FBQyxPQUFPLENBQUMsY0FBYyxDQUFDLGVBQWUsQ0FBQyxJQUFJLEtBQUssQ0FBQyxNQUFNLElBQUksQ0FBQyxJQUFJLEtBQUssQ0FBQyxDQUFDLENBQUMsSUFBSSxRQUFRLEVBQUU7b0JBQzdGLEdBQUcsQ0FBQyxNQUFNLENBQUMsR0FBRyxDQUFDLENBQUMsSUFBSSxDQUFDO3dCQUNuQixTQUFTLEVBQUUsSUFBSTt3QkFDZixZQUFZLEVBQUUsMkJBQTJCO3FCQUMxQyxDQUFDLENBQUE7aUJBQ0g7Z0JBSWMscUJBQU0sU0FBSSxDQUFDLE9BQU8sRUFBRSxDQUFDLEtBQUssQ0FBQyxxSUFJWixLQUFLLENBQUMsQ0FBQyxDQUFDLGFBQ2xDLENBQUMsRUFBQTs7Z0JBTEMsTUFBTSxHQUFHLFNBS1Y7Z0JB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