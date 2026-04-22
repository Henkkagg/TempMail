function interpolate(text: string, emailAddress: string): string {
    return text.replace("{emailAddress}", emailAddress)
}

function ServiceInfo({ emailAddress }: { emailAddress: string }) {
    const questions = [
        { q: import.meta.env.VITE_Q1, a: import.meta.env.VITE_A1 },
        { q: import.meta.env.VITE_Q2, a: import.meta.env.VITE_A2 },
        { q: import.meta.env.VITE_Q3, a: import.meta.env.VITE_A3 },
        { q: import.meta.env.VITE_Q4, a: import.meta.env.VITE_A4 },
        { q: import.meta.env.VITE_Q5, a: import.meta.env.VITE_A5 },
        { q: import.meta.env.VITE_Q6, a: import.meta.env.VITE_A6 },
        { q: import.meta.env.VITE_Q7, a: import.meta.env.VITE_A7 },
        { q: import.meta.env.VITE_Q8, a: import.meta.env.VITE_A8 },
        { q: import.meta.env.VITE_Q9, a: import.meta.env.VITE_A9 },
    ].filter(pair => pair.q && pair.a)

    return (
        <div className="flex flex-col">
            <span className="font-bold">{import.meta.env.VITE_WELCOME}</span>
            {questions.map((pair, index) => (
                <div key={index}>
                    <span className="font-bold mt-4 block">{pair.q}</span>
                    <span className="mt-2 block">{interpolate(pair.a, emailAddress)}</span>
                </div>
            ))}
        </div>
    )
}

export default ServiceInfo