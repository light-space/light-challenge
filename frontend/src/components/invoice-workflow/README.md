# Invoice Workflow Component

A modular React component system for handling invoice submission and approval routing.

## Structure

```
invoice-workflow/
├── index.tsx                    # Main InvoiceWorkflow component
├── types.ts                     # TypeScript interfaces and types
├── constants.ts                 # App constants (departments, etc.)
├── utils.ts                     # Utility functions (formatting, validation)
├── components/
│   ├── index.ts                # Barrel exports for clean imports
│   ├── InvoiceForm.tsx         # Form for invoice submission
│   ├── InvoiceResponse.tsx     # Response/result display
│   ├── ErrorAlert.tsx          # Error message component
│   └── StatusIcon.tsx          # Status icons and styling utilities
└── README.md                   # This documentation
```

## Components

### InvoiceWorkflow (Main Component)
- **Purpose**: Orchestrates the entire invoice workflow
- **Responsibilities**: State management, API calls, component coordination
- **Props**: None (top-level component)

### InvoiceForm
- **Purpose**: Handles user input for invoice details
- **Responsibilities**: Form validation, input formatting, submission
- **Props**: `formData`, `setFormData`, `onSubmit`, `loading`

### InvoiceResponse
- **Purpose**: Displays workflow execution results
- **Responsibilities**: Response formatting, rule information display
- **Props**: `response`

### ErrorAlert
- **Purpose**: Shows error messages to users
- **Responsibilities**: Error display with consistent styling
- **Props**: `error`

### StatusIcon
- **Purpose**: Provides visual status indicators
- **Responsibilities**: Icon selection, color coding, status text
- **Props**: `response`

## Best Practices Implemented

1. **Single Responsibility**: Each component has one clear purpose
2. **Clean Interfaces**: Well-defined TypeScript interfaces
3. **Reusability**: Components can be easily reused or modified
4. **Separation of Concerns**: Logic, UI, types, and constants are separated
5. **Barrel Exports**: Clean import statements through index files
6. **Type Safety**: Full TypeScript coverage with proper typing

## Usage

```tsx
import InvoiceWorkflow from '@/components/invoice-workflow';

function App() {
  return <InvoiceWorkflow />;
}
```

The component handles all internal state management and API communication automatically. 