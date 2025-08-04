import { CheckCircle, XCircle, AlertCircle } from 'lucide-react';
import { WorkflowResponse } from '../types';

interface StatusIconProps {
    response: WorkflowResponse;
}

export const StatusIcon = ({ response }: StatusIconProps) => {
    if (response.success) {
        return <CheckCircle className="h-5 w-5 text-green-600" />;
    }
    if (response.error?.type === 'NO_RULES_MATCHED') {
        return <AlertCircle className="h-5 w-5 text-yellow-600" />;
    }
    return <XCircle className="h-5 w-5 text-red-600" />;
};

export const getStatusColor = (response: WorkflowResponse): string => {
    if (response.success) return 'border-green-200 bg-green-50';
    if (response.error?.type === 'NO_RULES_MATCHED') return 'border-yellow-200 bg-yellow-50';
    return 'border-red-200 bg-red-50';
};

export const getStatusTitle = (response: WorkflowResponse): string => {
    if (response.success) return 'Approval Routed Successfully';
    if (response.error?.type === 'NO_RULES_MATCHED') return 'No Routing Rules Matched';
    return 'Approval Failed';
}; 