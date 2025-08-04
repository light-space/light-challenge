import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { WorkflowResponse } from '../types';
import { StatusIcon, getStatusColor, getStatusTitle } from './StatusIcon';

interface InvoiceResponseProps {
    response: WorkflowResponse;
}

export const InvoiceResponse = ({ response }: InvoiceResponseProps) => {
    return (
        <Card className={`border-2 ${getStatusColor(response)}`}>
            <CardHeader>
                <CardTitle className="flex items-center gap-2">
                    <StatusIcon response={response} />
                    {getStatusTitle(response)}
                </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
                <div className="prose prose-sm max-w-none">
                    <p className="text-gray-700 text-base leading-relaxed">
                        {response.message}
                    </p>
                </div>

                {response.rule && (
                    <div className="border-t pt-4">
                        <h4 className="font-semibold text-gray-900 mb-2">Applied Rule</h4>
                        <div className="bg-white p-3 rounded border">
                            <p className="font-medium text-green-700">{response.rule.rule_name}</p>
                            <p className="text-sm text-gray-600 mt-1">Rule ID: {response.rule.rule_id}</p>
                        </div>
                    </div>
                )}

                {response.error && response.error.details && (
                    <div className="border-t pt-4">
                        <h4 className="font-semibold text-gray-900 mb-2">Additional Information</h4>
                        <p className="text-sm text-gray-600">{response.error.details}</p>
                    </div>
                )}

                <div className="border-t pt-4 flex justify-between items-center text-xs text-gray-500">
                    <span>Request ID: {response.tracing.workflow_execution_id.slice(-8)}</span>
                    <span>{new Date().toLocaleString()}</span>
                </div>
            </CardContent>
        </Card>
    );
}; 