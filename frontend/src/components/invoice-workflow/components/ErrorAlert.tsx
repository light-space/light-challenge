import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { XCircle } from 'lucide-react';

interface ErrorAlertProps {
    error: string;
}

export const ErrorAlert = ({ error }: ErrorAlertProps) => {
    return (
        <Alert variant="destructive">
            <XCircle className="h-4 w-4" />
            <AlertTitle>Submission Failed</AlertTitle>
            <AlertDescription>{error}</AlertDescription>
        </Alert>
    );
}; 