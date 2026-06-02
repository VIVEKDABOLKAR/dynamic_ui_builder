import { ActionRegistry, ActionSchema } from '../types/JsonSchema';
import { ResolvedActionSchema } from '../types/JsonSchemaFormily';

export default function ActionDispacther(
	act: ActionSchema,
	actRegiestery: ActionRegistry = {}
): ResolvedActionSchema | null {
	if (!act?.ref) {
		return null;
	}

	const config = actRegiestery[act.ref];
	if (!config) {
		console.warn(`Action not found for ref: ${act.ref}`);
		return null;
	}

	return {
		...config,
		event: act.event,
		ref: act.ref,
		condition: act.condition,
	};
}

export function resolveComponentActions(
	actions: ActionSchema[] = [],
	actRegiestery: ActionRegistry = {}
): ResolvedActionSchema[] {
	return actions
		.map((act) => ActionDispacther(act, actRegiestery))
		.filter((action): action is ResolvedActionSchema => Boolean(action));
}
