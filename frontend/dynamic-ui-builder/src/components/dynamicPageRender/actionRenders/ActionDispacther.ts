
import { ActionRegistry, ActionSchema } from '../types/JsonSchema';
import { ResolvedActionSchema } from '../types/JsonSchemaFormily';
import ExecuteAction from './ExecuteAction';

// export default function ActionDispacther(
// 	act: ActionSchema,
// 	actRegiestery: ActionRegistry = {}
// ): ResolvedActionSchema | null {
// 	if (!act?.ref) {
// 		return null;
// 	}

// 	const config = actRegiestery[act.ref];
// 	if (!config) {
// 		console.warn(`Action not found for ref: ${act.ref}`);
// 		return null;
// 	}

// 	return {
// 		...config,
// 		event: act.event,
// 		ref: act.ref,
// 		condition: act.condition,
// 	};
// }


export function resolveComponentActions(
	actions: ActionSchema[] = [],
	actRegiestery: ActionRegistry = {},
    ctx: {}
): ResolvedActionSchema {
	
    const result: ResolvedActionSchema = {};

    actions.forEach((action) => {
        //null check
        if (!action.event || !action.ref) {
            return;
        }

        //override same action event
         result[action.event] =  () => ExecuteAction(action.ref, action.condition, actRegiestery, ctx);
    });

    console.log('handler :- ' , result)

    return result;

}
