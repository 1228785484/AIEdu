// build/webpack.base.conf.js
module.exports = {
  /** 其他与本次改动无关的配置 */
  resolve: {
    /** other code */
    extensions: ['.js', '.vue', '.json', '.ts'], // 添加 .ts 扩展名
  },
   module: {
    rules: [
      /** other code */
      {
        test: /\.tsx?$/,
        use: [
          { loader: 'babel-loader' },
          {
            loader: 'ts-loader',
            exclude: /node_modules/,
            options: {
              transpileOnly: true,
              appendTsSuffixTo: [
                '\\.vue$'
              ],
              happyPackMode: false
            }
          }
        ]
      }
    ]
  }
}
