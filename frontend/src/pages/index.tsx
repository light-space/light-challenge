import Head from 'next/head'
import InvoiceWorkflow from '@/components/invoice-workflow'

export default function Home() {
  return (
    <>
      <Head>
        <title>Invoice Approval System | Light</title>
        <meta name="description" content="Automated invoice approval routing system" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link rel="icon" href="/favicon.ico" />
      </Head>
      <main className="min-h-screen bg-gray-50">
        <InvoiceWorkflow />
      </main>
    </>
  )
}
