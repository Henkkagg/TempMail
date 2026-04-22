import { useState } from "react"
import { useEffect } from "react"

function Background() {
    const [iconSize, setIconSize] = useState({ width: 80, height: 80 })

    useEffect(() => {
        function calculate() {
            const targetSize = 80
            const cols = Math.round(window.innerWidth / targetSize)
            const rows = Math.round(window.innerHeight / targetSize)
            
            setIconSize({
                width: window.innerWidth / cols,
                height: window.innerHeight / rows
            })
        }
        calculate()
        window.addEventListener('resize', calculate)
        return () => window.removeEventListener('resize', calculate)
    }, [])

    const cols = Math.round(window.innerWidth / iconSize.width)
    const rows = Math.round(window.innerHeight / iconSize.height)

    return (
        <div className="fixed inset-0 z-[-1] grid"
             style={{
                gridTemplateColumns: `repeat(${cols}, 1fr)`,
                gridTemplateRows: `repeat(${rows}, 1fr)`
             }}>
            {Array(cols * rows).fill(null).map((_, i) => (
                <img key={i} src="/icon.svg" className="w-full h-full" />
            ))}
        </div>
    )
}
export default Background