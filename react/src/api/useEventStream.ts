import { useEffect, useState } from "react";
import getToken from "./getToken";

interface Email {
    id: number
    sender: string
    recipient: string
    subject: string
    body: string
}

export function useEventStream() {

    const [tokenReady, setTokenReady] = useState(false)
    const [emailAddress, setEmailAddress] = useState<string | null>(null)
    const [emails, setEmails] = useState<Email[] | null>(null)

    useEffect(() => {
        getToken().then((email) => {
            setEmailAddress(email)
            setTokenReady(true)
            console.log(email)
        })
        const refreshInterval = setInterval(getToken, 10000)
        return () => clearInterval(refreshInterval)
    }, [])

    useEffect(() => {
        if (!tokenReady) return
        const source = new EventSource("/api/events", { withCredentials: true })
        source.addEventListener("email", (e: MessageEvent) => {
            if (e.data === "no emails") {
                setEmails([])
            } else {
                const email: Email = JSON.parse(e.data)
                setEmails(prev => {
                    const current = prev ?? []
                    if (current.some(existing => existing.id === email.id)) return current
                    return [email, ...current]
                })
            }
        })
        return () => source.close()
    }, [tokenReady])

    return { emailAddress, emails }
}

export default useEventStream