import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css' // <-- É ESTA LINHA QUE PUXA O ESTILO DO TAILWIND
import App from './App.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)