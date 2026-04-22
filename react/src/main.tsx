import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import Box from './components/Box'
import Background from './components/Background'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <div className="flex min-h-screen min-w-screen items-center justify-center">
      <Background />
      <Box></Box>
    </div>
  </StrictMode>,
)
