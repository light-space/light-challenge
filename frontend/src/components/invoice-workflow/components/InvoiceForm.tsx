import React from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Checkbox } from '@/components/ui/checkbox';
import { Clock } from 'lucide-react';
import { FormData } from '../types';
import { DEPARTMENTS } from '../constants';
import { formatCurrency, isValidAmountInput } from '../utils';

interface InvoiceFormProps {
    formData: FormData;
    setFormData: React.Dispatch<React.SetStateAction<FormData>>;
    onSubmit: (e: React.FormEvent) => void;
    loading: boolean;
}

export const InvoiceForm = ({ formData, setFormData, onSubmit, loading }: InvoiceFormProps) => {
    const handleAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        if (isValidAmountInput(value)) {
            setFormData({ ...formData, amount: value });
        }
    };

    return (
        <Card className="border-2">
            <CardHeader>
                <CardTitle className="flex items-center gap-2">
                    <Clock className="h-5 w-5" />
                    New Invoice Submission
                </CardTitle>
                <CardDescription>
                    Enter the invoice details below to route it for approval
                </CardDescription>
            </CardHeader>
            <CardContent>
                <form onSubmit={onSubmit} className="space-y-6">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div className="space-y-2">
                            <Label htmlFor="amount" className="text-sm font-medium">
                                Invoice Amount <span className="text-red-500">*</span>
                            </Label>
                            <div className="relative">
                                <span className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500">$</span>
                                <Input
                                    id="amount"
                                    type="text"
                                    value={formData.amount}
                                    onChange={handleAmountChange}
                                    placeholder="0.00"
                                    className="pl-7 text-lg"
                                    required
                                />
                            </div>
                            {formData.amount && (
                                <p className="text-sm text-gray-600">
                                    {formatCurrency(formData.amount)}
                                </p>
                            )}
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="department" className="text-sm font-medium">
                                Department <span className="text-red-500">*</span>
                            </Label>
                            <Select
                                value={formData.department}
                                onValueChange={(value) => setFormData({ ...formData, department: value })}
                            >
                                <SelectTrigger id="department" className="text-lg">
                                    <SelectValue placeholder="Select department" />
                                </SelectTrigger>
                                <SelectContent>
                                    {DEPARTMENTS.map((dept) => (
                                        <SelectItem key={dept} value={dept}>
                                            {dept}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>
                    </div>

                    <div className="flex items-center space-x-3">
                        <Checkbox
                            id="manager-approval"
                            checked={formData.requires_manager_approval}
                            onCheckedChange={(checked) =>
                                setFormData({ ...formData, requires_manager_approval: checked === true })
                            }
                        />
                        <Label htmlFor="manager-approval" className="text-sm font-medium cursor-pointer">
                            This invoice requires manager approval
                        </Label>
                    </div>

                    <Button
                        type="submit"
                        disabled={loading || !formData.department || !formData.amount}
                        className="w-full md:w-auto px-8 py-3 text-lg"
                    >
                        {loading ? (
                            <>
                                <Clock className="mr-2 h-4 w-4 animate-spin" />
                                Processing...
                            </>
                        ) : (
                            'Submit for Approval'
                        )}
                    </Button>
                </form>
            </CardContent>
        </Card>
    );
}; 