import MarkdownIt from 'markdown-it'
import Shiki from '@shikijs/markdown-it'

class MarkdownRenderer {
  constructor() {
    // 使用 window 对象来保持单例
    if (window.__markdownRenderer) {
      return window.__markdownRenderer
    }
    window.__markdownRenderer = this
    this.renderer = null
    this.initializationPromise = null
  }

  async initialize() {
    // 如果已经在初始化中，返回现有的 promise
    if (this.initializationPromise) {
      return this.initializationPromise
    }

    // 如果已经初始化完成，直接返回
    if (this.renderer) {
      return this.renderer
    }

    // 创建新的初始化 promise
    this.initializationPromise = (async () => {
      console.log('[MarkdownRenderer] Initializing...')
      try {
        const baseMarkdown = new MarkdownIt({
          html: true,
          breaks: true,
          linkify: true
        })

        // 配置 Shiki
        const shikiPlugin = await Shiki({
          theme: 'github-dark'
        })
        
        await baseMarkdown.use(shikiPlugin)
        this.renderer = baseMarkdown
        console.log('[MarkdownRenderer] Initialization successful')
        return this.renderer
      } catch (error) {
        console.error('[MarkdownRenderer] Initialization failed:', error)
        // 使用基本的 markdown-it 作为后备
        this.renderer = new MarkdownIt({
          html: true,
          breaks: true,
          linkify: true
        })
        return this.renderer
      } finally {
        this.initializationPromise = null
      }
    })()

    return this.initializationPromise
  }

  async render(text) {
    const renderer = await this.initialize()
    return renderer.render(text)
  }
}

// 导出单例
export const markdownRenderer = new MarkdownRenderer()
