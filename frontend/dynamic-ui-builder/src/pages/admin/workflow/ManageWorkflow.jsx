import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import toast from 'react-hot-toast'
import { FaArrowDown, FaArrowUp, FaEdit, FaPlus, FaTrash } from 'react-icons/fa'
import { MenuItem, Select, Switch } from '@mui/material'
import {
  getWorkflowSteps,
  createWorkflowStep,
  updateWorkflowStep,
  deleteWorkflowStep,
  getWorkflowConfigurations,
  createWorkflowConfiguration,
  updateWorkflowConfiguration,
  deleteWorkflowConfiguration,
  getWorkflowConfigurationsList,
} from '../../../api/workflowApi'
import WorkflowStepDialog from '../../../components/admin/workflow/WorkflowStepDialog'
import { useFacility } from '../../../context/FacilityV2Context'

export default function ManageWorkflow() {
  const [steps, setSteps] = useState([])
  const [configs, setConfigs] = useState([]) // sorted by sequence
  const [loading, setLoading] = useState(true)

  const [stepDialogOpen, setStepDialogOpen] = useState(false)
  const [editingStep, setEditingStep] = useState(null)
  const [busyId, setBusyId] = useState(null)

  const [selectedStepToAdd, setSelectedStepToAdd] = useState('')

  const { selectedFacility } = useFacility()

  const loadAll = async () => {
    setLoading(true)
    try {
      const [stepsData, configsData] = await Promise.all([
        getWorkflowSteps(),
        getWorkflowConfigurationsList(),
      ])
      setSteps(stepsData || [])
      console.log(configsData)
      setConfigs((configsData || []).slice().sort((a, b) => a.sequence - b.sequence))
    } catch {
      toast.error('Failed to load workflow data')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadAll()
  }, [selectedFacility])

  //---------------------------
  // Workflow Step (master data) actions
  //---------------------------

  const handleAddStep = () => {
    setEditingStep(null)
    setStepDialogOpen(true)
  }

  const handleEditStep = (step) => {
    setEditingStep(step)
    setStepDialogOpen(true)
  }

  const handleSaveStep = async (formData) => {
    try {
      if (editingStep) {
        await updateWorkflowStep(editingStep.id, {
          name: formData.name,
          description: formData.description,
        })
        toast.success('Workflow step updated')
      } else {
        await createWorkflowStep(formData)
        toast.success('Workflow step created')
      }
      setStepDialogOpen(false)
      loadAll()
    } catch (err) {
      toast.error(err?.response?.data?.message || err?.response?.data || 'Failed to save step')
    }
  }

  const handleDeleteStep = async (step) => {
    if (!window.confirm(`Delete workflow step "${step.name}"? It must not be part of the active configuration.`)) return
    setBusyId(`step-${step.id}`)
    try {
      await deleteWorkflowStep(step.id)
      toast.success('Workflow step deleted')
      loadAll()
    } catch (err) {
      toast.error(err?.response?.data?.message || err?.response?.data || 'Failed to delete step')
    } finally {
      setBusyId(null)
    }
  }

  //---------------------------
  // Workflow Configuration (order + active) actions
  //---------------------------

  const unconfiguredSteps = steps.filter(
    (s) => !configs.some((c) => c.workflowStepId === s.id)
  )

  const handleAddToWorkflow = async () => {
    if (!selectedStepToAdd) return
    setBusyId('add')
    try {
      await createWorkflowConfiguration({
        workflowStepId: Number(selectedStepToAdd),
        sequence: null,
        active: true,
      })
      toast.success('Step added to workflow')
      setSelectedStepToAdd('')
      loadAll()
    } catch (err) {
      toast.error(err?.response?.data?.message || err?.response?.data || 'Failed to add step to workflow')
    } finally {
      setBusyId(null)
    }
  }

  const handleToggleActive = async (config) => {
    setBusyId(`toggle-${config.id}`)
    try {
      await updateWorkflowConfiguration(config.id, {
        sequence: config.sequence,
        active: !config.active,
      })
      toast.success(config.active ? 'Step disabled' : 'Step enabled')
      loadAll()
    } catch (err) {
      toast.error(err?.response?.data?.message || err?.response?.data || 'Failed to update step')
    } finally {
      setBusyId(null)
    }
  }

  const handleRemoveFromWorkflow = async (config) => {
    if (!window.confirm(`Remove "${config.workflowStepName}" from the workflow?`)) return
    setBusyId(`remove-${config.id}`)
    try {
      await deleteWorkflowConfiguration(config.id)
      toast.success('Step removed from workflow')
      loadAll()
    } catch (err) {
      toast.error(err?.response?.data?.message || err?.response?.data || 'Failed to remove step')
    } finally {
      setBusyId(null)
    }
  }

  const handleMove = async (index, direction) => {
    const targetIndex = index + direction
    if (targetIndex < 0 || targetIndex >= configs.length) return

    const current = configs[index]
    const target = configs[targetIndex]

    setBusyId(`move-${current.id}`)
    try {
      // Swap sequence values between the two rows
      await Promise.all([
        updateWorkflowConfiguration(current.id, { sequence: target.sequence, active: current.active }),
        updateWorkflowConfiguration(target.id, { sequence: current.sequence, active: target.active }),
      ])
      loadAll()
    } catch (err) {
      toast.error(err?.response?.data?.message || err?.response?.data || 'Failed to reorder steps')
    } finally {
      setBusyId(null)
    }
  }

  return (
    <div className="flex flex-col gap-8">
      <div>
        <div className="flex items-center gap-1 text-xs text-slate-400">
          <Link to="/admin_panel/overview" className="hover:text-slate-600">Admin</Link>
          <span>&gt;</span>
          <span className="text-slate-500">Workflow Configuration</span>
        </div>
        <h1 className="mt-2 text-2xl font-bold text-slate-900">Job Workflow Configuration</h1>
        <p className="mt-1 text-sm text-slate-500">
          Control which steps run in the Job Order workflow, and in what order — no deployment required.
          Changes only affect Job Orders created after the change; in-flight jobs keep the steps they started with.
        </p>
      </div>

      {loading ? (
        <div className="p-6 text-sm text-slate-500">Loading workflow…</div>
      ) : (
        <>
          {/* Active Workflow */}
          <div className="rounded-2xl border border-slate-200 bg-white shadow-sm">
            <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-100 px-5 py-3">
              <h2 className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
                Active Workflow
              </h2>

              <div className="flex items-center gap-2">
                <Select
                  size="small"
                  displayEmpty
                  value={selectedStepToAdd}
                  onChange={(e) => setSelectedStepToAdd(e.target.value)}
                  sx={{ minWidth: 220 }}
                  disabled={unconfiguredSteps.length === 0}
                >
                  <MenuItem value="">
                    {unconfiguredSteps.length === 0 ? 'All steps added' : 'Select a step to add...'}
                  </MenuItem>
                  {unconfiguredSteps.map((s) => (
                    <MenuItem key={s.id} value={s.id}>
                      {s.name} ({s.code})
                    </MenuItem>
                  ))}
                </Select>

                <button
                  onClick={handleAddToWorkflow}
                  disabled={!selectedStepToAdd || busyId === 'add'}
                  className="flex items-center gap-1.5 rounded-full bg-cyan-400 px-4 py-2 text-sm font-semibold hover:bg-cyan-300 disabled:opacity-50"
                >
                  <FaPlus size={12} /> Add to Workflow
                </button>
              </div>
            </div>

            {configs.length === 0 ? (
              <div className="p-6 text-sm text-slate-500">
                <p>
                  No steps configured. Add a workflow step below, then add it to the workflow here.
                </p>

                <p className="mt-3 rounded-md border border-blue-200 bg-blue-50 px-3 py-2 text-blue-700">
                 Currently, the global workflow configuration is applied for this facility.
                </p>
              </div>

            ) : (
              <table className="w-full text-sm">
                <thead className="bg-slate-50 text-left text-xs font-semibold uppercase text-slate-500">
                  <tr>
                    <th className="px-5 py-3 w-24">Order</th>
                    <th className="px-5 py-3">Step</th>
                    <th className="px-5 py-3">Code</th>
                    <th className="px-5 py-3">Active</th>
                    <th className="px-5 py-3 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {configs.map((config, index) => (
                    <tr key={config.id} className={!config.active ? 'opacity-50' : ''}>
                      <td className="px-5 py-3">
                        <div className="flex items-center gap-1">
                          <button
                            onClick={() => handleMove(index, -1)}
                            disabled={index === 0 || busyId === `move-${config.id}`}
                            className="rounded p-1 text-slate-500 hover:bg-slate-100 disabled:opacity-30"
                            title="Move up"
                          >
                            <FaArrowUp size={12} />
                          </button>
                          <button
                            onClick={() => handleMove(index, 1)}
                            disabled={index === configs.length - 1 || busyId === `move-${config.id}`}
                            className="rounded p-1 text-slate-500 hover:bg-slate-100 disabled:opacity-30"
                            title="Move down"
                          >
                            <FaArrowDown size={12} />
                          </button>
                          <span className="ml-1 text-xs text-slate-400">{index + 1}</span>
                        </div>
                      </td>
                      <td className="px-5 py-3 font-medium text-slate-900">{config.workflowStepName}</td>
                      <td className="px-5 py-3 text-slate-500">{config.workflowStepCode}</td>
                      <td className="px-5 py-3">
                        <Switch
                          size="small"
                          checked={config.active}
                          onChange={() => handleToggleActive(config)}
                          disabled={busyId === `toggle-${config.id}`}
                        />
                      </td>
                      <td className="px-5 py-3 text-right">
                        <button
                          onClick={() => handleRemoveFromWorkflow(config)}
                          disabled={busyId === `remove-${config.id}`}
                          className="rounded-full bg-red-500 p-2 text-white hover:bg-red-400 disabled:opacity-60"
                          title="Remove from workflow"
                        >
                          <FaTrash size={14} />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>

          {/* Workflow Steps (master data) */}
          <div className="rounded-2xl border border-slate-200 bg-white shadow-sm">
            <div className="flex items-center justify-between border-b border-slate-100 px-5 py-3">
              <div>
                <h2 className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
                  Workflow Steps
                </h2>
                <p className="mt-1 text-xs text-slate-500">
                  Master list of step types the system can execute.
                </p>
              </div>

              <button
                onClick={handleAddStep}
                className="rounded-full bg-slate-900 px-4 py-2 text-sm font-semibold text-white hover:bg-slate-700"
              >
                + Add Step
              </button>
            </div>

            {steps.length === 0 ? (
              <div className="p-6 text-sm text-slate-500">No workflow steps yet.</div>
            ) : (
              <table className="w-full text-sm">
                <thead className="bg-slate-50 text-left text-xs font-semibold uppercase text-slate-500">
                  <tr>
                    <th className="px-5 py-3">Code</th>
                    <th className="px-5 py-3">Name</th>
                    <th className="px-5 py-3">Description</th>
                    <th className="px-5 py-3 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {steps.map((step) => (
                    <tr key={step.id}>
                      <td className="px-5 py-3 font-mono text-xs text-slate-600">{step.code}</td>
                      <td className="px-5 py-3 font-medium text-slate-900">{step.name}</td>
                      <td className="px-5 py-3 text-slate-500">{step.description || '—'}</td>
                      <td className="px-5 py-3 text-right">
                        <div className="flex justify-end gap-2">
                          <button
                            onClick={() => handleEditStep(step)}
                            className="rounded-full bg-slate-900 p-2 text-white hover:bg-slate-700"
                            title="Edit"
                          >
                            <FaEdit size={14} />
                          </button>
                          <button
                            onClick={() => handleDeleteStep(step)}
                            disabled={busyId === `step-${step.id}`}
                            className="rounded-full bg-red-500 p-2 text-white hover:bg-red-400 disabled:opacity-60"
                            title="Delete"
                          >
                            <FaTrash size={14} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </>
      )}

      <WorkflowStepDialog
        open={stepDialogOpen}
        onClose={() => setStepDialogOpen(false)}
        onSave={handleSaveStep}
        editingStep={editingStep}
        existingCodes={steps.map((s) => s.code)}
      />
    </div>
  )
}
