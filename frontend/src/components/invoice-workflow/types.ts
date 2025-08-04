export interface WorkflowRequest {
    amount: number;
    department: string;
    requires_manager_approval: boolean;
}

export interface TracingInfo {
    workflow_execution_id: string;
    rule_result_id?: string;
}

export interface RuleInfo {
    rule_id: string;
    rule_name: string;
}

export interface ErrorInfo {
    type: 'VALIDATION_ERROR' | 'NO_RULES_MATCHED' | 'SYSTEM_ERROR';
    details?: string;
}

export interface WorkflowResponse {
    success: boolean;
    message: string;
    tracing: TracingInfo;
    rule?: RuleInfo;
    error?: ErrorInfo;
}

export interface FormData {
    amount: string;
    department: string;
    requires_manager_approval: boolean;
} 