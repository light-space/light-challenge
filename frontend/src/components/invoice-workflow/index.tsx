import React, { useState } from 'react';
import { FormData, WorkflowRequest, WorkflowResponse } from './types';
import { validateAmount } from './utils';
import { InvoiceForm, InvoiceResponse, ErrorAlert } from './components';

export default function InvoiceWorkflow() {
    const [formData, setFormData] = useState<FormData>({
        amount: '',
        department: '',
        requires_manager_approval: false,
    });

    const [response, setResponse] = useState<WorkflowResponse | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!formData.amount || !formData.department) {
            setError('Please fill in all required fields');
            return;
        }

        if (!validateAmount(formData.amount)) {
            setError('Please enter a valid amount greater than 0');
            return;
        }

        setLoading(true);
        setError(null);
        setResponse(null);

        try {
            const requestData: WorkflowRequest = {
                amount: parseFloat(formData.amount),
                department: formData.department.toLowerCase(),
                requires_manager_approval: formData.requires_manager_approval,
            };

            const res = await fetch('/api/workflow/execute', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
                body: JSON.stringify(requestData),
            });

            const contentType = res.headers.get('content-type');
            if (!contentType || !contentType.includes('application/json')) {
                const textResponse = await res.text();
                throw new Error(`Server error: ${textResponse.substring(0, 100)}...`);
            }

            const data: WorkflowResponse = await res.json();
            setResponse(data);

            if (!res.ok && res.status !== 400) {
                throw new Error(`Server error: ${data.message || 'Unknown error'}`);
            }
        } catch (err) {
            console.error('Error calling workflow API:', err);
            if (err instanceof Error) {
                setError(err.message);
            } else {
                setError('Unable to connect to the approval system');
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-4xl mx-auto p-6 space-y-6">
            <div className="text-center space-y-2">
                <h1 className="text-3xl font-bold text-gray-900">Invoice Approval System</h1>
                <p className="text-gray-600">Submit an invoice for automated approval routing</p>
            </div>

            <InvoiceForm
                formData={formData}
                setFormData={setFormData}
                onSubmit={handleSubmit}
                loading={loading}
            />

            {error && <ErrorAlert error={error} />}

            {response && <InvoiceResponse response={response} />}
        </div>
    );
} 