export const formatCurrency = (amount: string): string => {
    if (!amount) return '';
    const num = parseFloat(amount);
    if (isNaN(num)) return amount;
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
    }).format(num);
};

export const validateAmount = (amount: string): boolean => {
    if (!amount) return false;
    const num = parseFloat(amount);
    return !isNaN(num) && num > 0;
};

export const isValidAmountInput = (value: string): boolean => {
    // Allow empty string, digits, and one decimal point
    return value === '' || /^\d*\.?\d*$/.test(value);
}; 