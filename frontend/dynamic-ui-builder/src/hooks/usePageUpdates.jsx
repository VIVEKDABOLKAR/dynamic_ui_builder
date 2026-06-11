import { useEffect, useRef } from 'react'
import { Client } from '@stomp/stompjs'

export default function usePageUpdates(onUpdate) {
  const onUpdateRef = useRef(onUpdate)

  useEffect(() => {
    onUpdateRef.current = onUpdate
  }, [onUpdate])

  useEffect(() => {
    const client = new Client({
      brokerURL: 'ws://localhost:8080/ws-native',  // ← clean path
      reconnectDelay: 5000,

      onConnect: () => {
        console.log('✅ WebSocket connected')
        client.subscribe('/topic/page-updates', (message) => {
          try {
            const update = JSON.parse(message.body)
            onUpdateRef.current(update)
          } catch(error) {
            console.log(error)
            console.error('Failed to parse message')
          }
        })
      },

      onStompError: (frame) => {
        console.error('STOMP error', frame)
      },

      onDisconnect: () => console.log('WebSocket disconnected'),
    })

    client.activate()
    return () => client.deactivate()
  }, [])
}