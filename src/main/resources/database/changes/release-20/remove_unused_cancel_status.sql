DELETE FROM action.actionstate where statepk = 'CANCELLED';
DELETE FROM action.actionstate where statepk = 'CANCEL_PENDING';
DELETE FROM action.actionstate where statepk = 'CANCEL_SUBMITTED';
DELETE FROM action.actionstate where statepk = 'CANCELLING';