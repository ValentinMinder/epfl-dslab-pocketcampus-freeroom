package org.pocketcampus.plugin.freeroom.server.utils;

import org.pocketcampus.plugin.freeroom.shared.*;

/**
 * Checks that the request receive all well-formed and not corrupted (never
 * trust the user application!).
 * <p/>
 * Also useful for debug to detect quickly client bugs.
 *
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public class CheckRequests {
    public static FROccupancyReply checkFRRequest(FROccupancyRequest request) {
        FROccupancyReply reply = new FROccupancyReply();
        FRStatusCode status = FRStatusCode.HTTP_OK;
        String statusComment = "FRRequest : ";

        if (request == null) {
            status = FRStatusCode.HTTP_BAD_REQUEST;
            statusComment = "FRRequest is null;";
        } else {
            if (!request.isSetPeriod() || request.getPeriod() == null) {
                status = FRStatusCode.HTTP_BAD_REQUEST;
                statusComment += "FRPeriod is null;";
            }

            if (!request.isSetOnlyFreeRooms()) {
                status = FRStatusCode.HTTP_BAD_REQUEST;
                statusComment += "boolean onlyFreeRooms is not set;";
            }

            if (!request.isSetUserGroup()) {
                status = FRStatusCode.HTTP_BAD_REQUEST;
                statusComment += "User group is not set;";
            }
        }

        reply.setStatus(status);
        if (status != FRStatusCode.HTTP_OK) {
            reply.setStatusComment(statusComment);
        } else {
            reply.setStatusComment(FRStatusCode.HTTP_OK + "");
        }
        return reply;
    }

    public static FRAutoCompleteReply checkAutoCompleteRequest(
            FRAutoCompleteRequest request) {
        FRAutoCompleteReply reply = new FRAutoCompleteReply();
        FRStatusCode status = FRStatusCode.HTTP_OK;
        String statusComment = "AutoCompleteRequest : ";

        if (request == null) {
            status = FRStatusCode.HTTP_BAD_REQUEST;
            statusComment = "AutoCompleteRequest is null;";
        } else {
            if (!request.isSetConstraint() || request.getConstraint() == null) {
                status = FRStatusCode.HTTP_BAD_REQUEST;
                statusComment += "Constraint is null;";
            }

            if (!request.isSetUserGroup()) {
                status = FRStatusCode.HTTP_BAD_REQUEST;
                statusComment += "User group is not set;";
            }
        }

        reply.setStatus(status);
        if (status != FRStatusCode.HTTP_OK) {
            reply.setStatusComment(statusComment);
        } else {
            reply.setStatusComment(FRStatusCode.HTTP_OK + "");
        }
        return reply;
    }

    public static FRImWorkingReply checkImWorkingRequest(FRImWorkingRequest request) {
        FRImWorkingReply reply = new FRImWorkingReply();
        FRStatusCode status = FRStatusCode.HTTP_OK;
        String statusComment = "ImWorkingRequest : ";

        if (request == null) {
            status = FRStatusCode.HTTP_BAD_REQUEST;
            statusComment = "ImWorkingRequest is null;";
        } else {
            if (!request.isSetWork() || request.getWork() == null) {
                status = FRStatusCode.HTTP_BAD_REQUEST;
                statusComment += "work (WorkingOccupancy) is null;";
            }

            if (!request.isSetHash() || request.getHash() == null) {
                status = FRStatusCode.HTTP_BAD_REQUEST;
                statusComment += "Hash is not set;";
            }

            String workCheck = checkWorkingOccupancy(request.getWork());
            if (workCheck != null) {
                status = FRStatusCode.HTTP_BAD_REQUEST;
                statusComment += workCheck;
            }
        }

        reply.setStatus(status);
        if (status != FRStatusCode.HTTP_OK) {
            reply.setStatusComment(statusComment);
        } else {
            reply.setStatusComment(FRStatusCode.HTTP_OK + "");
        }
        return reply;
    }

    public static String checkWorkingOccupancy(FRWorkingOccupancy work) {
        boolean error = false;
        String comment = "WorkingOccupancy ";
        if (work == null) {
            comment += "is null;";
            return comment;
        }

        if (!work.isSetPeriod() || work.getPeriod() == null) {
            comment += "FRPeriod is not set;";
            error = true;
        }

        if (!work.isSetRoom() || work.getRoom() == null) {
            comment += "FRRoom is not set;";
            error = true;
        }

        if (error) {
            return comment;
        }
        return null;
    }

    public static FRWhoIsWorkingReply checkWhoIsWorkingRequest(
            FRWhoIsWorkingRequest request) {
        FRWhoIsWorkingReply reply = new FRWhoIsWorkingReply();
        FRStatusCode status = FRStatusCode.HTTP_OK;
        String statusComment = "WhoIsWorkingRequest : ";

        if (request == null) {
            status = FRStatusCode.HTTP_BAD_REQUEST;
            statusComment = "WhoIsWorkingRequest is null;";
        } else {
            if (!request.isSetRoomUID() || request.getRoomUID() == null) {
                status = FRStatusCode.HTTP_BAD_REQUEST;
                statusComment += "room ID  is null;";
            }

            if (!request.isSetPeriod() || request.getPeriod() == null) {
                status = FRStatusCode.HTTP_BAD_REQUEST;
                statusComment += "FRPeriod is not set;";
            }
        }

        reply.setStatus(status);
        if (status != FRStatusCode.HTTP_OK) {
            reply.setStatusComment(statusComment);
        } else {
            reply.setStatusComment(FRStatusCode.HTTP_OK + "");
        }
        return reply;
    }
}
