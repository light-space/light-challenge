# LLM Agent for Rule Generation

This package contains the LLM agent implementation for generating workflow rules based on user prompts using the [Koog AI framework](https://docs.koog.ai/) with proper [structured data processing](https://docs.koog.ai/structured-data/).

## Overview

The rule generation system uses:
- **Koog AI Framework v0.3.0**: For LLM agent creation and management
- **Structured Data Generation**: Using proper annotations and JSON schema generation
- **OpenAI GPT-4o**: As the underlying language model
- **Kotlinx Serialization**: For data structure serialization

## Components

### 1. Models (`models/` package)
All models are properly annotated for structured data generation:

- `LLMAction`: Action model with `@Serializable`, `@SerialName`, and `@LLMDescription` annotations
- `LLMRule`: Rule model with detailed field descriptions for the LLM
- `LLMRules`: Wrapper model for collections of rules

### 2. Agent (`RuleGenerationAgent`)
- Uses `promptExecutor.executeStructured()` method for structured responses
- Generates JSON schema using `JsonStructuredData.createJsonStructure<LLMRules>()`
- Provides example rules to help the LLM understand the format
- Includes retry logic and fixing model for error correction

### 3. Services
- `RuleGenerationService`: Core service for rule generation
- `RuleGenerationServiceImpl`: API implementation
- `AgentConfiguration`: Configuration and dependency injection

## Structured Data Implementation

Following the [Koog AI structured data documentation](https://docs.koog.ai/structured-data/):

### Data Structure Definition
```kotlin
@Serializable
@SerialName("LLMRule")
@LLMDescription("A workflow rule that defines conditions and actions for invoice processing")
data class LLMRule(
    @property:LLMDescription("Unique identifier for the rule (kebab-case format)")
    val id: String,
    // ... other fields with proper annotations
)
```

### JSON Schema Generation
```kotlin
private val rulesStructure = JsonStructuredData.createJsonStructure<LLMRules>(
    schemaFormat = JsonSchemaGenerator.SchemaFormat.JsonSchema,
    examples = listOf(LLMRules(exampleRules)),
    schemaType = JsonStructuredData.JsonSchemaType.SIMPLE
)
```

### Structured Response Request
```kotlin
val structuredResponse = promptExecutor.executeStructured(
    prompt = prompt {
        system(buildSystemPrompt())
        user("Generate workflow rules based on this description: $userPrompt")
    },
    structure = rulesStructure,
    mainModel = OpenAIModels.Chat.GPT4o,
    retries = 3,
    fixingModel = OpenAIModels.Chat.GPT4o
)
```

## Usage

### Environment Setup
Set the OpenAI API key as an environment variable:
```bash
export OPENAI_API_KEY="your-openai-api-key-here"
```

### API Endpoint
The endpoint is available at:
```
POST /workflow/generate-rules
```

Request body:
```json
{
  "prompt": "Your workflow description here"
}
```

Response:
```json
{
  "success": true,
  "message": "Successfully generated 5 workflow rules",
  "generatedRules": [
    {
      "id": "route-high-value-marketing-to-cmo",
      "name": "Route High-Value Marketing Invoice to CMO",
      "description": "Routes invoices over $10,000 for marketing purposes to the CMO via email",
      "priority": 100,
      "conditionExpression": "invoice.amount > 10000 && invoice.department == 'marketing'",
      "actionId": "send-email",
      "actionParameters": {
        "recipient": "cmo@example.com",
        "subject": "High-Value Invoice Approval Request",
        "body": "A new marketing invoice over $10,000 requires your approval."
      }
    }
  ],
  "executionId": "uuid",
  "error": null
}
```

### Example User Prompt
```
First, if an invoice is for more than $10,000, the system checks if it's a marketing purchase. If it is, the request is sent to the CMO via email. If not, it's sent to the CFO via Slack.
If the invoice is for $10,000 or less, the system then checks if it's over $5,000. For these mid-range invoices (between $5,001 and $10,000), it checks if a manager's approval is required. If so, a Finance manager gets the request via email. If not, it goes to any Finance team member via Slack.
Finally, any invoice for $5,000 or less is sent directly to any Finance team member via Slack.
```

## Key Features

### Proper Structured Data Usage
- **Annotations**: All data classes use `@Serializable`, `@SerialName`, and `@LLMDescription`
- **Examples**: Provides example rules to guide LLM understanding
- **Schema Generation**: Uses `JsonStructuredData.createJsonStructure<T>()`
- **Structured Requests**: Uses `executeStructured()` method with retry logic

### Error Handling & Reliability
- **Automatic Retries**: Up to 3 retries for structured response generation
- **Fixing Model**: Uses GPT-4o for output coercion and error correction
- **Validation**: Comprehensive input validation and error responses

### System Prompt Guidelines
The agent is configured with a comprehensive system prompt that:
- Understands invoice workflow terminology
- Generates proper condition expressions
- Uses standard communication channels (email/Slack)
- Assigns appropriate priorities to rules
- Follows business logic patterns

## Dependencies

- `ai.koog:koog-agents:0.3.0` - LLM agent framework
- `kotlinx-serialization-json:1.6.0` - Serialization for structured data
- `kotlinx-coroutines-core:1.7.3` - Async operations
- Standard project dependencies (Dropwizard, Jackson, etc.)

## Build Configuration

The serialization plugin is added to the build:
```kotlin
plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.10"
}
```

## Testing

Run tests with:
```bash
./gradlew test
```

The implementation follows [Koog AI best practices](https://docs.koog.ai/structured-data/#best-practices) for structured data processing. 